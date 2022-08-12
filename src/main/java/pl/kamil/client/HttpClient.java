package pl.kamil.client;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HttpClient {
  private final HttpUrl baseUrl;
  private final OkHttpClient client;
  private final String authToken;

  HttpClient(String authToken, HttpUrl baseUrl, OkHttpClient client) {
    this.authToken = authToken;
    this.baseUrl = baseUrl;
    this.client = client;
  }

  public Response newCall(Request.Builder requestBuilder) {
    requestBuilder.addHeader("Authorization", "Bearer " + authToken);
    try {
      var response = client.newCall(requestBuilder.build()).execute();
      if (response.isSuccessful()) {
        return response;
      }
      if (response.code() == 401) {
        throw new RuntimeException("Unauthorized");
      } else {
        throw new RuntimeException(Objects.requireNonNull(response.body()).string());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public CompletableFuture<Response> newAsyncCall(Request.Builder requestBuilder) {
    requestBuilder.addHeader("Authorization", "Bearer " + authToken);
    var responseFuture = new ResponseFuture();
    client.newCall(requestBuilder.build()).enqueue(responseFuture);
    return responseFuture.future;
  }

  public HttpUrl getBaseUrl() {
    return this.baseUrl;
  }

  private static class ResponseFuture implements Callback {
    private final CompletableFuture<Response> future = new CompletableFuture<>();

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
      future.completeExceptionally(e);
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) {
      if (response.isSuccessful()) {
        future.complete(response);
      }
      if (response.code() == 401) {
        future.completeExceptionally(new RuntimeException("Unauthorized"));
      }
    }
  }

  public static class HttpClientBuilder {
    private final String authToken;
    private HttpUrl baseUrl;
    private final OkHttpClient.Builder builder = new OkHttpClient.Builder();

    public HttpClientBuilder(String authToken) {
      this.authToken = authToken;
    }

    public HttpClientBuilder withBaseUrl(String baseUrl) {
      this.baseUrl = HttpUrl.get(baseUrl);
      return this;
    }

    public HttpClientBuilder withMyRequestInterceptor(MyRequestInterceptor requestInterceptor) {
      if (requestInterceptor != null) {
        builder.addInterceptor(
            new Interceptor() {
              @NotNull
              @Override
              public Response intercept(@NotNull Chain chain) throws IOException {
                var originalRequest = chain.request();
                var requestBody = Objects.requireNonNullElse(originalRequest.body(), null);
                var bodyContentType =
                    Objects.requireNonNullElse(requestBody.contentType(), null);
                // for simplification, I'm ommiting getting body, but it is possible to get it:
                // https://square.github.io/okhttp/features/interceptors/#rewriting-requests

                var intecreptedResponse =
                    requestInterceptor.intecrept(
                        new MyRequest(
                            originalRequest.url().url(),
                            originalRequest.method(),
                            bodyContentType != null ? bodyContentType.toString() : null,
                            requestBody.contentLength()));
                if (intecreptedResponse != null) {
                  return toResponse(intecreptedResponse);
                }
                // what if request was modified?
                return chain.proceed(originalRequest);
              }

              private Response toResponse(MyResponse intecreptedResponse) {
                // somehow convert our response to OkHttp
                return null;
              }
            });
      }
      return this;
    }

    public HttpClientBuilder withRequestInterceptor(Interceptor requestInterceptor) {
      this.builder.addInterceptor(requestInterceptor);
      return this;
    }

    public HttpClient build() {
      // enable HTTP and HTTP/2
      builder.protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1));
      this.baseUrl =
          Objects.requireNonNullElseGet(
              baseUrl,
              () ->
                  new HttpUrl.Builder()
                      .scheme("https")
                      .host("api.box.com")
                      .addPathSegment("2.0")
                      .build());
      if (baseUrl.isHttps()) {
        builder.connectionSpecs(List.of(ConnectionSpec.MODERN_TLS));
      }

      return new HttpClient(authToken, baseUrl, builder.build());
    }
  }
}
