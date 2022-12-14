package pl.kamil.folder;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kamil.client.HttpClient;
import pl.kamil.client.MyJsonMapper;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Ignore
class SynchronousFolderUnitTest {
  private FolderManager folderManager;
  private MockWebServer server;
  private final MyJsonMapper jsonMapper = MyJsonMapper.getInstance();

  @BeforeEach
  void setUp() {
    this.server = new MockWebServer();
    var baseUrl = String.format("http://%s:%d", this.server.getHostName(), this.server.getPort());
    HttpClient client = new HttpClient.HttpClientBuilder("FAKE_TOKEN").withBaseUrl(baseUrl).build();
    this.folderManager = new FolderManager(client);
  }

  @AfterEach
  void tearDown() throws IOException {
    this.server.shutdown();
  }

  @Test
  void get() throws InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-type", "application/json")
            .setBody(
                """
                {
                    "id": "fake",
                    "type": "folder"
                }
                """));
    var response = folderManager.getFolderInfo("fake");
    assertThat(response.getId()).isEqualTo("fake");
    var recordedRequest = this.server.takeRequest();
    assertThat(recordedRequest.getPath()).isEqualTo("/folders/fake");
  }

  @Test
  void deleteWithDispath() {
    var folderToRemove = "toRemove";
    this.server.setDispatcher(
        new Dispatcher() {
          @NotNull
          @Override
          public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
            if ("DELETE".equals(recordedRequest.getMethod())
                && ("/folders/" + folderToRemove).equals(recordedRequest.getPath())) {
              return new MockResponse().setResponseCode(204);
            }
            return new MockResponse()
                .setResponseCode(500)
                .setBody(
                    String.format(
                        "Request [%s] '%s' is not mapped",
                        recordedRequest.getMethod(), recordedRequest.getPath()));
          }
        });

    folderManager.deleteFolder(folderToRemove);
  }

  @Test
  void createWithInterceptor() {
    var httpClient =
        new HttpClient.HttpClientBuilder("FAKE_TOKEN")
            .withRequestInterceptor(
                chain ->
                    new Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_2)
                        .message("")
                        .code(200)
                        .body(
                            ResponseBody.create(
                                """
                                            {
                                                "id": "intercepted",
                                                "type": "folder"
                                            }
                                            """,
                                MediaType.parse("application/json")))
                        .build())
            .build();
    var folderManager = new FolderManager(httpClient);
    var response = folderManager.getFolderInfo("12345");
    assertThat(response.getId()).isEqualTo("intercepted");
  }

  @Test
  void retriesGet() {
    server.enqueue(new MockResponse().setResponseCode(429).setBody("Excedding allowed rate limit"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-type", "application/json")
            .setBody(
                """
                {
                    "id": "fake",
                    "type": "folder"
                }
                """));
    var response = folderManager.getFolderInfo("fake");
    assertThat(response.getId()).isEqualTo("fake");
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
    assertThatThrownBy(() -> folderManager.getFolderInfo("fake"))
        .hasMessage("Excedding allowed rate limit");
  }

  @Test
  void assertCreateBody() {
    var folderName = "new_folder";
    var parentId = "122345";
    var httpClient =
        new HttpClient.HttpClientBuilder("FAKE_TOKEN")
            .withRequestInterceptor(
                chain -> {
                  var buffer = new Buffer();
                  chain.request().body().writeTo(buffer);
                  var jsonNode = jsonMapper.parse(buffer.readUtf8());
                  assertThat(jsonNode.get("name").asText()).isEqualTo(folderName);
                  assertThat(jsonNode.get("parent").get("id").asText()).isEqualTo(parentId);
                  return new Response.Builder()
                      .request(chain.request())
                      .protocol(Protocol.HTTP_2)
                      .message("")
                      .code(200)
                      .body(
                          ResponseBody.create(
                              """
                                          {
                                              "id": "intercepted",
                                              "type": "folder"
                                          }
                                          """,
                              MediaType.parse("application/json")))
                      .build();
                })
            .build();
    var folderManager = new FolderManager(httpClient);
    folderManager.createFolder(new CreateFolderRequest(folderName, parentId));
  }
}
