package org.thetasinner.data.model;

import lombok.Data;

@Data
public class Video {
  private String id;
  private String title;
  private TypedUrl url;
}
