package pl.kamil;

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

    public HttpClient(String authToken, HttpUrl baseUrl) {
        this.authToken = authToken;
        this.baseUrl = baseUrl;
        var builder = new OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1));
        if (baseUrl.isHttps()) {
            builder.connectionSpecs(List.of(ConnectionSpec.MODERN_TLS));
        }
        this.client = builder.build();
    }

    public HttpClient(String authToken) {
        this(authToken, new HttpUrl.Builder()
                .scheme("https")
                .host("api.box.com")
                .addPathSegment("2.0")
                .build()
        );
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
}
