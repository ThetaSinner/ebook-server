package org.thetasinner.data.model;

import java.util.ArrayList;
import java.util.List;

public class Library {
  private List<Book> books = new ArrayList<>();
  private List<Video> videos = new ArrayList<>();

  public List<Book> getBooks() {
    return books;
  }

  public List<Video> getVideos() {
    return videos;
  }
}
