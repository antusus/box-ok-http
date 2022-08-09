package pl.kamil;

import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class Uploader {

  private String authToken;
  private OkHttpClient httpClient = new OkHttpClient();
  private static String ATTRIBUTES =
      "{\n"
          + "  \"name\": \"%s\",\n"
          + "  \"parent\": {\n"
          + "    \"id\": \"0\"\n"
          + "  },\n"
          + "  \"content_created_at\": \"2012-12-12T10:53:43-08:00\",\n"
          + "  \"content_modified_at\": \"2012-12-12T10:53:43-08:00\"\n"
          + "}";

  public Uploader(String authToken) {
    this.authToken = authToken;
  }

  public void uploadFile(File file, String name) {
    MultipartBody multipartBody =
        new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("attributes", String.format(ATTRIBUTES, name))
            .addFormDataPart(
                "file", name, RequestBody.create(file, MediaType.parse("application/octet-stream")))
            .build();
    executePostRequest("https://upload.box.com/api/2.0/files/content", multipartBody);
  }

  public void uploadAvatar(String userId, File file) {
    String name = "avatar.jpeg";
    MultipartBody multipartBody =
        new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "pic", file.getName(), RequestBody.create(file, MediaType.parse("image/jpeg")))
            .build();
    executePostRequest(String.format("https://api.box.com/2.0/users/%s/avatar", userId), multipartBody);
  }

  private void executePostRequest(String url, RequestBody body) {
    Request request =
        new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + authToken)
            .addHeader("Cookie", "ff_enable_user_avatar_api=yes")
            .post(body)
            .build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String resposeString = response.body().string();
        throw new RuntimeException(
            "API Failed with code [" + response.code() + "]: " + resposeString);
      }
      System.out.println(response.body().string());
    } catch (IOException e) {
      System.err.println(e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
