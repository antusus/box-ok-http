package pl.kamil;

public abstract class UsingDeveloperTokenTests {
    public static String getDeveloperToken() {
        return System.getenv("DEVELOPER_TOKEN");
    }
}
