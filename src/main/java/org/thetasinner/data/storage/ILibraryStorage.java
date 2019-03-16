package org.thetasinner.data.storage;

import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Library;

import java.awt.image.RenderedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ILibraryStorage {
  Library load(String libraryName);

  void save(String libraryName, Library library);

  void create(String name);

  StorageResult store(String name, MultipartFile file) throws StorageException, IOException;

  void storeCover(String libraryName, String id, RenderedImage image) throws IOException;

  void deleteBook(String id, String name) throws IOException;

  List<String> getLibraries();

  FileInputStream getBookInputStream(Book book) throws FileNotFoundException;

  void storeCover(Book book, MultipartFile cover) throws StorageException;

  FileInputStream getCoverInputStream(Book book) throws FileNotFoundException;

  Book recover(String name, String id);
}
