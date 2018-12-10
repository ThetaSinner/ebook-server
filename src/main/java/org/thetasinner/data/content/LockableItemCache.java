package org.thetasinner.data.content;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class LockableItemCache<T> {
  private HashMap<String, LockableItem<T>> inner = new HashMap<>();

  void put(String name, T item) {
    inner.put(name, new LockableItem<>(item));
  }

  LockableItem<T> get(String name) {
    return inner.get(name);
  }

  boolean has(String name) {
    return inner.containsKey(name);
  }

  void remove(String name) {
    inner.remove(name);
  }
}

