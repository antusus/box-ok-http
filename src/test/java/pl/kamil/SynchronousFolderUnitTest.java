package pl.kamil;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
class SynchronousFolderUnitTest {
    private FolderManager folderManager;
    private MockWebServer server;

    @BeforeEach
    void setUp() {
        this.server = new MockWebServer();
        var baseUrl = new HttpUrl.Builder()
                .scheme("http")
                .host(this.server.getHostName())
                .port(this.server.getPort())
                .build();
        HttpClient client = new HttpClient("FAKE_TOKEN", baseUrl);
        this.folderManager = new FolderManager(client);
    }

    @AfterEach
    void tearDown() throws IOException {
        this.server.shutdown();
    }

    @Test
    void get() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-type", "application/json")
                .setBody("""
                        {
                            "id": "fake"
                        }
                        """));
        var response = folderManager.getFolderInfo("fake");
        assertThat(response.get("id").asText()).isEqualTo("fake");
        var recordedRequest = this.server.takeRequest();
        assertThat(recordedRequest.getPath()).isEqualTo("/folders/fake");
    }

    @Test
    void deleteWithDispath() {
        var folderToRemove = "toRemove";
        this.server.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                if ("DELETE".equals(recordedRequest.getMethod()) && ("/folders/" + folderToRemove).equals(recordedRequest.getPath())) {
                    return new MockResponse().setResponseCode(204);
                }
                return new MockResponse()
                        .setResponseCode(500).
                        setBody(String.format("Request [%s] '%s' is not mapped", recordedRequest.getMethod(), recordedRequest.getPath()));
            }
        });

        folderManager.deleteFolder(folderToRemove);
    }
}