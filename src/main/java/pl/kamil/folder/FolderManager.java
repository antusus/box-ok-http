package pl.kamil.folder;

import okhttp3.*;
import pl.kamil.client.HttpClient;
import pl.kamil.client.MyJsonMapper;

import java.util.Objects;

public class FolderManager {
  private static final MyJsonMapper objectMapper = MyJsonMapper.getInstance();
  private final HttpClient httpClient;
  private final HttpUrl baseUrl;

  public FolderManager(HttpClient client) {
    this.httpClient = client;
    this.baseUrl = client.getBaseUrl().newBuilder().addPathSegment("folders").build();
  }

  public FolderStandard getFolderInfo(String folderId) {
    var request =
        new Request.Builder().url(baseUrl.newBuilder().addPathSegment(folderId).build()).get();
    return objectMapper.deserialize(responseAsJson(request), FolderStandard.class);
  }

  public FolderStandard createFolder(CreateFolderRequest folder) {
    Request.Builder request =
        new Request.Builder()
            .url(baseUrl)
            .post(
                RequestBody.create(
                    objectMapper.serailize(folder), MediaType.parse("applcation/json")));
    return objectMapper.deserialize(responseAsJson(request), FolderStandard.class);
  }

  public void deleteFolder(String folderId) {
    var request =
        new Request.Builder().url(baseUrl.newBuilder().addPathSegment(folderId).build()).delete();
    newCall(request).close();
  }

  private String responseAsJson(Request.Builder request) {
    try (var responseBody = newCall(request)) {
      return objectMapper.toJsonString(Objects.requireNonNull(responseBody.body()).byteStream());
    }
  }

  private Response newCall(Request.Builder request) {
    return httpClient.newCall(request);
  }
}
