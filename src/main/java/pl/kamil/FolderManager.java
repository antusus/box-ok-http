package pl.kamil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import pl.kamil.client.HttpClient;

import java.io.IOException;
import java.util.Objects;

public class FolderManager {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final HttpClient httpClient;
  private final HttpUrl baseUrl;

  public FolderManager(HttpClient client) {
    this.httpClient = client;
    this.baseUrl = client.getBaseUrl().newBuilder().addPathSegment("folders").build();
  }

  public JsonNode getFolderInfo(String folderId) {
    var request =
        new Request.Builder().url(baseUrl.newBuilder().addPathSegment(folderId).build()).get();
    return responseAsJson(request);
  }

  public JsonNode createFolder(CreateFolderRequest folder) {
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

  public void deleteFolder(String folderId) {
    var request =
        new Request.Builder().url(baseUrl.newBuilder().addPathSegment(folderId).build()).delete();
    newCall(request).close();
  }

  private JsonNode responseAsJson(Request.Builder request) {
    try (var responseBody = newCall(request)) {
      return objectMapper.readTree(Objects.requireNonNull(responseBody.body()).byteStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Response newCall(Request.Builder request) {
    return httpClient.newCall(request);
  }
}
