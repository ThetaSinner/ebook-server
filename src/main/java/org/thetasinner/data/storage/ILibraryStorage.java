package org.thetasinner.data.storage;

import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;

import java.util.List;

public interface ILibraryStorage {
    void load(String name);

    void save(String name);

    void create(String name);

    List<Book> getBooks(String name);

    Book createBook(String name, String url, TypedUrl.Type type);
}
