package pl.kamil.folder;

import java.util.HashMap;
import java.util.Map;

public class CreateFolderRequest {
  public final String name;
  public final Map<String, String> parent = new HashMap<>();

  public CreateFolderRequest(String name, String parentId) {
    this.name = name;
    parent.put("id", parentId);
  }
}
