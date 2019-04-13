package org.thetasinner.data.model;

import lombok.Data;

import java.util.Date;

@Data
public class Video {
  private String id;
  private String title;
  private String description;
  private Date releaseDate;
  private TypedUrl url;
  private Long duration;
  private VideoMetadata videoMetadata;
}
