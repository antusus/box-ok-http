package pl.kamil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class CreateFolderRequest {
    public final String name;
    public final JsonNode parent;

    public CreateFolderRequest(String name, String parentId) {
        this.name = name;
        this.parent = JsonNodeFactory.instance.objectNode().put("id", parentId);
    }
}
