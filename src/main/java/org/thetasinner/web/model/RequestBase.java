package org.thetasinner.web.model;

import lombok.Data;

@Data
public class RequestBase<T> {
  private String name;
  private T request;
}
