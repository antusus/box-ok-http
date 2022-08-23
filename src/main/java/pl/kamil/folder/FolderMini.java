package pl.kamil.folder;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class FolderMini implements Serializable {
  private final String type = "folder";
  private String id;
  private String etag;
  private String name;

  @JsonProperty("sequence_id")
  private String sequenceId;

  // we have to create one if we do not use getters/setters
  // however this does not scale well when we have lots of attributes
  //  @JsonCreator
  //  public FolderMini(
  //      @JsonProperty("id") String id,
  //      @JsonProperty("etag") String etag,
  //      @JsonProperty("name") String name,
  //      @JsonProperty("sequence_id") String sequenceId) {
  //    this.id = id;
  //    this.etag = etag;
  //    this.name = name;
  //    this.sequenceId = sequenceId;
  //  }

  public String getId() {
    return id;
  }

  void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public String getEtag() {
    return etag;
  }

  void setEtag(String etag) {
    this.etag = etag;
  }

  public String getName() {
    return name;
  }

  void setName(String name) {
    this.name = name;
  }

  public String getSequenceId() {
    return sequenceId;
  }

  void setSequenceId(String sequenceId) {
    this.sequenceId = sequenceId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FolderMini that = (FolderMini) o;
    return id.equals(that.id)
        && etag.equals(that.etag)
        && name.equals(that.name)
        && sequenceId.equals(that.sequenceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, etag, name, sequenceId);
  }

  @Override
  public String toString() {
    return "FolderMini{"
        + "id='"
        + id
        + '\''
        + ", type='"
        + type
        + '\''
        + ", etag='"
        + etag
        + '\''
        + ", name='"
        + name
        + '\''
        + ", sequenceId='"
        + sequenceId
        + '\''
        + '}';
  }
}
