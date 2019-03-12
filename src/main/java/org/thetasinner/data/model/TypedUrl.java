package org.thetasinner.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypedUrl implements Serializable {
  private String value;
  private Type type;

  public enum Type {
    LocalManaged,
    LocalUnmanaged,
    WebLink,
    Other
  }
}
