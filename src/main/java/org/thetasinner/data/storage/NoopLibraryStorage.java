package org.thetasinner.data.storage;

import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;

import java.util.List;

public class NoopLibraryStorage implements ILibraryStorage {
    @Override
    public void load(String name) {

    }

    @Override
    public void save(String name) {

    }

    @Override
    public void create(String name) {

    }

    @Override
    public List<Book> getBooks(String name) {
        return null;
    }

    @Override
    public Book createBook(String name, String url, TypedUrl.Type type) {
        return null;
    }
}
