package pl.kamil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class FolderManager {
    private final HttpClient httpClient;
    private final static String BASE_URL = "https://api.box.com/2.0/folders";
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public FolderManager(HttpClient client) {
        this.httpClient = client;
    }

    public JsonNode getFolderInfo(String folderId) {
        var request = new Request.Builder().url(BASE_URL + "/" + folderId);
        return responseAsJson(request);
    }

    public JsonNode createFolder(CreateFolderRequest folder) {
        try {
            Request.Builder createFolderRequest = new Request.Builder()
                    .url(BASE_URL)
                    .post(RequestBody.create(
                            objectMapper.writeValueAsBytes(folder),
                            MediaType.parse("applcation/json")
                    ));
            return responseAsJson(createFolderRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFolder(String folderId) {
        newCall(new Request.Builder().url(BASE_URL + "/" + folderId).delete()).close();
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
