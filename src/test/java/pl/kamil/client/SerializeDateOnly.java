package pl.kamil.client;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

public class SerializeDateOnly {
  @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
  private Date date;

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
