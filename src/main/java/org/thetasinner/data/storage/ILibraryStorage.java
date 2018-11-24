package org.thetasinner.data.storage;

import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.web.model.BookUpdateRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ILibraryStorage {
    void save(String name, Boolean unload);

    void create(String name);

    List<Book> getBooks(String name);

    Book createBook(String name, String url, TypedUrl.Type type);

    void store(String name, MultipartFile file) throws StorageException, IOException;

    Book updateBook(String id, String name, BookUpdateRequest bookUpdateRequest);

    void deleteBook(String id, String name);

    List<String> getLibraries();

    FileInputStream getBookInputStream(String id, String name) throws FileNotFoundException;

    void storeCover(String id, String name, MultipartFile cover) throws StorageException;

    FileInputStream getCoverInputStream(String bookId, String libraryName) throws FileNotFoundException;
}
