package org.thetasinner.web.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LibraryChangeEvent {
    private String libraryName;
    private ChangeEventData eventData;
}
