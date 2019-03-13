package org.thetasinner.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookMetadata implements Serializable {
  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  private List<String> tags = new ArrayList<>();
  private Byte rating;

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public Byte getRating() {
    return rating;
  }

  public void setRating(Byte rating) {
    this.rating = rating;
  }
}
