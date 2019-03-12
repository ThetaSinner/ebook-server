package org.thetasinner.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Book implements Serializable {
  private String id;
  private String isbn;
  private TypedUrl url;
  private String title;
  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  private List<String> authors = new ArrayList<>();
  private String publisher;
  private Date datePublished;
  private String description;
  private List<TypedUrl> covers = new ArrayList<>();
  private BookMetadata metadata;
}
