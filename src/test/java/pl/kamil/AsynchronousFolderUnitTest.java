package pl.kamil;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kamil.client.HttpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Ignore
class AsynchronousFolderUnitTest {
  private AsyncFolderManager folderManager;
  private MockWebServer server;

  @BeforeEach
  void setUp() {
    this.server = new MockWebServer();
    var baseUrl = String.format("http://%s:%d", this.server.getHostName(), this.server.getPort());
    HttpClient client = new HttpClient.HttpClientBuilder("FAKE_TOKEN").withBaseUrl(baseUrl).build();
    this.folderManager = new AsyncFolderManager(client);
  }

  @AfterEach
  void tearDown() throws IOException {
    this.server.shutdown();
  }

  @Test
  void retriesGet() throws InterruptedException, ExecutionException, TimeoutException {
    server.enqueue(new MockResponse().setResponseCode(429).setBody("Excedding allowed rate limit"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-type", "application/json")
            .setBody(
                """
                                    {
                                        "id": "fake"
                                    }
                                    """));
    var response = folderManager.getFolderInfo("fake").get(100, MILLISECONDS);
    assertThat(response.get("id").asText()).isEqualTo("fake");
  }

  @Test
  void retriesFails() {
    IntStream.range(1, 7)
        .forEach(
            i ->
                server.enqueue(
                    new MockResponse()
                        .setResponseCode(429)
                        .setBody("Excedding allowed rate limit")));
    assertThatThrownBy(() -> folderManager.getFolderInfo("fake").get(100, MILLISECONDS))
        // with async execution exceptions are wrappen in `java.util.concurrent.ExecutionException`
        .hasCauseInstanceOf(RuntimeException.class)
        .hasMessageContaining("Excedding allowed rate limit");
  }
}
