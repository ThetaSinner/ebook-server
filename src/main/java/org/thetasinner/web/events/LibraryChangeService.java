package org.thetasinner.web.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class LibraryChangeService {
    private final ApplicationEventPublisher eventPublisher;

    public LibraryChangeService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish(String libraryName, ChangeEventData eventData) {
        eventPublisher.publishEvent(new LibraryChangeEvent(libraryName, eventData));
    }
}
