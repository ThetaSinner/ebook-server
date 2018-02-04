package org.thetasinner.data.storage.file;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class FileCache<T> {
    private HashMap<String, T> inner = new HashMap<>();

    public void put(String name, T item) {
        inner.put(name, item);
    }

    public T get(String name) {
        return inner.get(name);
    }

    public boolean has(String name) {
        return inner.containsKey(name);
    }
}
