package org.thetasinner.data.model;

public class TypedUrl {
  private String value;
  private Type type;

  public TypedUrl() {
    /* Needed for deserialization */
  }

  public TypedUrl(String value, Type type) {
    this.value = value;
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public enum Type {
    LocalManaged,
    LocalUnmanaged,
    WebLink,
    Other
  }
}
