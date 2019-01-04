package org.thetasinner.web.controller;

import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/events")
public class EventsController {
  private static final long SSE_TIMEOUT = 3_000_000L; // 5 Minutes

  private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> subscriptions = new ConcurrentHashMap<>();

  @RequestMapping(value = "/subscribe/{libraryName}", method = RequestMethod.GET)
  public SseEmitter subscribe(@PathVariable(name = "libraryName") String libraryName) {
    SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

    CopyOnWriteArrayList<SseEmitter> list = new CopyOnWriteArrayList<>();
    subscriptions.putIfAbsent(libraryName, list);
    subscriptions.get(libraryName).add(emitter);

    Runnable callback = () -> this.subscriptions.get(libraryName).remove(emitter);
    emitter.onCompletion(callback);
    emitter.onTimeout(callback);

    return emitter;
  }

  @EventListener
  public void onLibraryChanged(LibraryChangeEvent libraryChangeEvent) {
    String libraryName = libraryChangeEvent.getLibraryName();
    CopyOnWriteArrayList<SseEmitter> sseEmitters = subscriptions.get(libraryName);
    if (sseEmitters == null) {
      return;
    }

    List<SseEmitter> deadEmitters = new ArrayList<>();
    sseEmitters.forEach(emitter -> {
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

    sseEmitters.removeAll(deadEmitters);
  }
}
