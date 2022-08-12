package pl.kamil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kamil.client.HttpClient;

import static org.assertj.core.api.Assertions.assertThat;

class SynchronousFolderDeveloperTokenTest extends UsingDeveloperTokenTests {
  private final HttpClient client = new HttpClient.HttpClientBuilder(getDeveloperToken()).build();
  private FolderManager folderManager;

  @BeforeEach
  void setUp() {
    this.folderManager = new FolderManager(client);
  }

  @Test
  void get() {
    var response = folderManager.getFolderInfo("0");
    assertThat(response.get("id").asText()).isEqualTo("0");
  }

  @Test
  void postAndDelete() {
    var newFolderName = "Kamil Test with ok-http";
    var folder = folderManager.createFolder(new CreateFolderRequest(newFolderName, "0"));

    assertThat(folder.get("name").asText()).isEqualTo(newFolderName);

    folderManager.deleteFolder(folder.get("id").asText());
  }
}
