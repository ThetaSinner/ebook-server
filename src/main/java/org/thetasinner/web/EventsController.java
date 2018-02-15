package org.thetasinner.web;

import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.thetasinner.web.events.LibraryChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/events")
public class EventsController {
    private static final long SSE_TIMEOUT = 3_000_000L; // 5 Minutes

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> subscriptions = new ConcurrentHashMap<>();

    @RequestMapping(value = "/subscribe/{libraryName}", method = RequestMethod.GET)
    public SseEmitter subscribe(@PathVariable(name = "libraryName") String libraryName) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        if (!subscriptions.containsKey(libraryName)) {
            CopyOnWriteArrayList<SseEmitter> list = new CopyOnWriteArrayList<>();
            list.add(emitter);
            this.subscriptions.put(libraryName, list);
        }
        else {
            this.subscriptions.get(libraryName).add(emitter);
        }

        emitter.onCompletion(() -> this.subscriptions.get(libraryName).remove(emitter));
        emitter.onTimeout(() -> this.subscriptions.get(libraryName).remove(emitter));

        return emitter;
    }

    @EventListener
    public void onLibraryChanged(LibraryChangeEvent libraryChangeEvent) {
        String libraryName = libraryChangeEvent.getLibraryName();
        List<SseEmitter> deadEmitters = new ArrayList<>();
        subscriptions.get(libraryName).forEach(emitter -> {
            try {
                SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                        .name("change")
                        .reconnectTime(0)
                        .data(libraryChangeEvent.getEventData());
                emitter.send(eventBuilder);
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });

        subscriptions.get(libraryName).removeAll(deadEmitters);
    }
}
