package pl.kamil;

import okhttp3.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class HttpClient {
    private final OkHttpClient client;
    private final String authToken;

    public HttpClient(String authToken) {
        this.authToken = authToken;
        this.client = new OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();
    }

    public Response newCall(Request.Builder requestBuilder){
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
}
