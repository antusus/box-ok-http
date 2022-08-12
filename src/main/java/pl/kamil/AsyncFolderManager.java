package pl.kamil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import pl.kamil.client.HttpClient;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class AsyncFolderManager {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final HttpClient httpClient;
  private final HttpUrl baseUrl;

  public AsyncFolderManager(HttpClient client) {
    this.httpClient = client;
    this.baseUrl = client.getBaseUrl().newBuilder().addPathSegment("folders").build();
  }

  public CompletableFuture<JsonNode> getFolderInfo(String folderId) {
    var request =
        new Request.Builder().url(baseUrl.newBuilder().addPathSegment(folderId).build()).get();
    return responseAsJson(request);
  }

  public CompletableFuture<JsonNode> createFolder(CreateFolderRequest folder) {
    try {
      Request.Builder request =
          new Request.Builder()
              .url(baseUrl)
              .post(
                  RequestBody.create(
                      objectMapper.writeValueAsBytes(folder), MediaType.parse("applcation/json")));
      return responseAsJson(request);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public CompletableFuture<Void> deleteFolder(String folderId) {
    var request =
        new Request.Builder().url(baseUrl.newBuilder().addPathSegment(folderId).build()).delete();
    return newCall(request).thenApply(response -> null);
  }

  private CompletableFuture<JsonNode> responseAsJson(Request.Builder request) {
    return newCall(request)
        .thenApply(
            response -> {
              try {
                return objectMapper.readTree(Objects.requireNonNull(response.body()).byteStream());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  private CompletableFuture<Response> newCall(Request.Builder request) {
    return httpClient.newAsyncCall(request);
  }
}
