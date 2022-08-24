package pl.kamil.folder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kamil.UsingDeveloperTokenTests;
import pl.kamil.client.HttpClient;

import java.util.Date;

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
    assertThat(response.getId()).isEqualTo("0");
    assertThat(response.getEtag()).isNull();
    assertThat(response.getName()).isEqualTo("All Files");
    assertThat(response.getSequenceId()).isNull();
    assertThat(response.getCreatedAt()).isNull();
  }

  @Test
  void postAndDelete() {
    var newFolderName = "Kamil Test with ok-http";
    var folder = folderManager.createFolder(new CreateFolderRequest(newFolderName, "0"));
    assertThat(folder.getId()).isNotNull();
    assertThat(folder.getName()).isEqualTo(newFolderName);
    assertThat(folder.getCreatedAt()).isCloseTo(new Date(), 1000L);

    folderManager.deleteFolder(folder.getId());
  }

  @Test
  void getItems() {
    var items = folderManager.items("0");

    // we expect that there are some folders
    assertThat(items.getEntries().stream().filter(i -> i instanceof FolderMini)).isNotEmpty();
  }
}
