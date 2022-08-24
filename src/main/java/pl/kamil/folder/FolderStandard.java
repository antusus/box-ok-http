package pl.kamil.folder;

import java.util.Date;

public class FolderStandard extends FolderMini {

  private Date createdAt;
  //  private final UserMini createdBy;
  //  private final String description;
  //  private final String itemStatus;

  public Date getCreatedAt() {
    return createdAt;
  }

  void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }
}
