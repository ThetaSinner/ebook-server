package org.thetasinner.web.model;

import lombok.Data;

@Data
public class BookAddRequest {
  private String url;
  private Type type;

  public enum Type {
    LocalUnmanaged,
    WebLink,
    Other
  }
}
