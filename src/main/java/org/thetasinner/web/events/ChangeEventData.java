package org.thetasinner.web.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeEventData {
  private ChangeType changeType;
  private String bookId;

  public enum ChangeType {
    BookCreated,
    BookUpdated,
    BookDeleted,
    VideoCreated,
    VideoUpdated
  }
}
