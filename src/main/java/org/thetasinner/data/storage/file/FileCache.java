package org.thetasinner.data.storage.file;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class FileCache<T> {
  private HashMap<String, LockableItem<T>> inner = new HashMap<>();

  void put(String name, T item) {
    inner.put(name, new LockableItem<T>(item));
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

class LockableItem<T> {
  private T item;
  private ReentrantLock lock = new ReentrantLock();

  LockableItem(T item) {
    this.item = item;
  }

  T getItem() {
    return item;
  }

  void lock() {
    lock.lock();
  }

  void unlock() {
    // Once this item is unlocked it should not be used. If Java could describe it, the item reference should
    // never be copied!
    item = null;
    lock.unlock();
  }
}
