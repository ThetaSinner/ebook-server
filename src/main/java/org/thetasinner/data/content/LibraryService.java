package org.thetasinner.data.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thetasinner.data.model.Library;
import org.thetasinner.data.storage.ILibraryStorage;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class LibraryService {
  private static final Logger LOG = LoggerFactory.getLogger(LibraryService.class);

  private final ReentrantLock acquireLibraryLock = new ReentrantLock();

  private final ILibraryStorage libraryStorage;

  private LockableItemCache<Library> cache;

  public LibraryService(LockableItemCache<Library> cache, ILibraryStorage libraryStorage) {
    LOG.trace("Creating library service");

    this.cache = cache;
    this.libraryStorage = libraryStorage;
  }

  LockableItem<Library> getLibrary(String libraryName) {
    LOG.debug("Getting library by name [{}]", libraryName);

    if (!cache.has(libraryName)) {
      load(libraryName);
    }

    return cache.get(libraryName);
  }

  public void create(String libraryName) {
    LOG.debug("Creating library with name [{}]", libraryName);

    libraryStorage.create(libraryName);
  }

  public List<String> getLibraries() {
    LOG.trace("Getting libraries");

    return libraryStorage.getLibraries();
  }

  public void save(String libraryName, boolean unload) {
    LOG.debug("Saving library with name [{}]", libraryName);

    // Ignore save requests for libraries which are not loaded.
    if (!cache.has(libraryName)) return;

    libraryStorage.save(libraryName, cache.get(libraryName).getItem());

    if (unload) {
      cache.remove(libraryName);
    }
  }

  private void load(String libraryName) {
    LOG.debug("Loading library with name [{}]", libraryName);

    try {
      acquireLibraryLock.lock();

      if (cache.has(libraryName)) {
        return;
      }

      var library = this.libraryStorage.load(libraryName);
      cache.put(libraryName, library);
    } finally {
      acquireLibraryLock.unlock();
    }
  }
}
