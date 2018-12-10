package org.thetasinner.data.content;

import java.util.concurrent.locks.ReentrantLock;

public class LockableItem<T> {
  private T item;
  private ReentrantLock lock = new ReentrantLock();

  LockableItem(T item) {
    this.item = item;
  }

  public T getItem() {
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
