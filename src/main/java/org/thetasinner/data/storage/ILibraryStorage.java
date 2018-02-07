package org.thetasinner.data.storage;

import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.web.model.BookUpdateRequest;

import java.util.List;

public interface ILibraryStorage {
    void load(String name);

    void save(String name);

    void create(String name);

    List<Book> getBooks(String name);

    Book createBook(String name, String url, TypedUrl.Type type);

    void store(String name, MultipartFile file) throws StorageException;

    Book updateBook(String id, String name, BookUpdateRequest bookUpdateRequest);

    void deleteBook(String id, String name);
}
