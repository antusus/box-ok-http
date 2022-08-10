package pl.kamil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class AsyncFolderManager {
    private final HttpClient httpClient;
    private final static HttpUrl BASE_URL = new HttpUrl.Builder()
            .scheme("https")
            .host("api.box.com")
            .addPathSegment("2.0")
            .addPathSegment("folders")
            .build();
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public AsyncFolderManager(HttpClient client) {
        this.httpClient = client;
    }

    public CompletableFuture<JsonNode> getFolderInfo(String folderId) {
        var request = new Request.Builder()
                .url(BASE_URL.newBuilder().addPathSegment(folderId).build())
                .get();
        return responseAsJson(request);
    }

    public CompletableFuture<JsonNode> createFolder(CreateFolderRequest folder) {
        try {
            Request.Builder request = new Request.Builder()
                    .url(BASE_URL)
                    .post(RequestBody.create(
                            objectMapper.writeValueAsBytes(folder),
                            MediaType.parse("applcation/json")
                    ));
            return responseAsJson(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: it should be Void
    public CompletableFuture<Response> deleteFolder(String folderId) {
        var request = new Request.Builder()
                .url(BASE_URL.newBuilder().addPathSegment(folderId).build())
                .delete();
        return newCall(request);
    }

    private CompletableFuture<JsonNode> responseAsJson(Request.Builder request) {
        return newCall(request).thenApply(response -> {
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
