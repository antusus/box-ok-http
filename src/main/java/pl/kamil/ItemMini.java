package pl.kamil;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.kamil.file.FileMini;
import pl.kamil.folder.FolderMini;
import pl.kamil.weblink.WeblinkMini;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = FolderMini.class, name = "folder"),
  @JsonSubTypes.Type(value = FileMini.class, name = "file"),
  @JsonSubTypes.Type(value = WeblinkMini.class, name = "weblink")
})
public class ItemMini {
  private final String type;
  private String id;
  private String name;
  private String etag;

  @JsonProperty("sequence_id")
  private String sequenceId;

  protected ItemMini(String type) {
    this.type = type;
  }

  public String getId() {
    return id;
  }

  protected void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  protected void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public String getEtag() {
    return etag;
  }

  protected void setEtag(String etag) {
    this.etag = etag;
  }

  public String getSequenceId() {
    return sequenceId;
  }

  protected void setSequenceId(String sequenceId) {
    this.sequenceId = sequenceId;
  }

  @Override
  public String toString() {
    return "ItemMini{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", type='"
        + type
        + '\''
        + ", etag='"
        + etag
        + '\''
        + ", sequenceId='"
        + sequenceId
        + '\''
        + '}';
  }
}
