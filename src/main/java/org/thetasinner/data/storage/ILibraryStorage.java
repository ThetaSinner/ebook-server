package org.thetasinner.data.storage;

import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Library;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ILibraryStorage {
  Library load(String libraryName);

  void save(String libraryName, Library library);

  void create(String name);

  Book store(String name, MultipartFile file) throws StorageException, IOException;

  void deleteBook(String id, String name);

  List<String> getLibraries();

  FileInputStream getBookInputStream(Book book) throws FileNotFoundException;

  void storeCover(Book book, MultipartFile cover) throws StorageException;

  FileInputStream getCoverInputStream(Book book) throws FileNotFoundException;
}
