package pl.kamil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SynchronousFolderDeveloperTokenTest {
    private static final String DEVELOPER_TOKEN = "nXr4QOjcwmtIZFItP7yX2pP5EJqYw4rO";
    private final HttpClient client = new HttpClient(DEVELOPER_TOKEN);
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
        var folder = folderManager.createFolder(
                new CreateFolderRequest(newFolderName, "0")
        );

        assertThat(folder.get("name").asText()).isEqualTo(newFolderName);

        folderManager.deleteFolder(folder.get("id").asText());
    }
}