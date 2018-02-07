package org.thetasinner.data.storage;

import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.web.model.BookUpdateRequest;

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

    @Override
    public void store(String name, MultipartFile file) throws StorageException {

    }

    @Override
    public Book updateBook(String id, String name, BookUpdateRequest bookUpdateRequest) {
        return null;
    }

    @Override
    public void deleteBook(String id, String name) {

    }
}
