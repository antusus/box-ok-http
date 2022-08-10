package pl.kamil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class AsynchronousFolderDeveloperTokenTest extends UsingDeveloperTokenTests {
    private final HttpClient client = new HttpClient(getDeveloperToken());
    private AsyncFolderManager folderManager;

    @BeforeEach
    void setUp() {
        this.folderManager = new AsyncFolderManager(client);
    }

    @Test
    void get() throws ExecutionException, InterruptedException, TimeoutException {
        var response = folderManager.getFolderInfo("0").get(2000, MILLISECONDS);
        assertThat(response.get("id").asText()).isEqualTo("0");
    }

    @Test
    void postAndDelete() throws ExecutionException, InterruptedException, TimeoutException {
        var newFolderName = "Kamil Test with ok-http";
        var folder = folderManager.createFolder(
                new CreateFolderRequest(newFolderName, "0")
        ).get(2000, MILLISECONDS);

        assertThat(folder.get("name").asText()).isEqualTo(newFolderName);

        folderManager.deleteFolder(folder.get("id").asText()).get(2000, MILLISECONDS);
    }
}