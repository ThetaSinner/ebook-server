package org.thetasinner.web.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeEventData {
    private ChangeType changeType;
    private String bookId;

    public enum ChangeType {
        BookCreated,
        BookUpdated,
        BookDeleted
    }
}
