package pl.kamil;

import java.util.List;

public class Items<T> {
  private List<T> entries;
  private int limit;

  public List<T> getEntries() {
    return entries;
  }

  protected void setEntries(List<T> entries) {
    this.entries = entries;
  }

  public int getLimit() {
    return limit;
  }

  protected void setLimit(int limit) {
    this.limit = limit;
  }

  @Override
  public String toString() {
    return "Items{" + "entries=" + entries + ", limit=" + limit + '}';
  }
}
