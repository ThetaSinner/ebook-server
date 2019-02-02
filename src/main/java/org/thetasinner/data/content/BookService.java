package org.thetasinner.data.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.exception.EBookNotFoundException;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.StorageException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {
  private static final Logger LOG = LoggerFactory.getLogger(BookService.class);

  private final LibraryService libraryService;

  private final ILibraryStorage libraryStorage;

  public BookService(LibraryService libraryService, ILibraryStorage libraryStorage) {
    this.libraryService = libraryService;
    this.libraryStorage = libraryStorage;
  }

  public Book createBook(String libraryName, String url, TypedUrl.Type type) {
    LOG.trace("Creating book in library [{}] for resource [{}]", libraryName, url);

    Book book = new Book();
    book.setId(UUID.randomUUID().toString());
    book.setUrl(new TypedUrl(url, type));

    var library = libraryService.getLibrary(libraryName);
    library.getItem().getBooks().add(book);

    return book;
  }

  public List<Book> getBooks(String libraryName) {
    LOG.trace("Getting books for library [{}]", libraryName);

    var library = libraryService.getLibrary(libraryName).getItem();
    return library.getBooks();
  }

  public Book updateBook(String id, String name, String bookUpdateRequest) throws JsonPatchException, IOException {
    LOG.info("Updating book with id [{}] in library [{}]", id, name);

    Book book = getBook(name, id);

    var mapper = new ObjectMapper();
    var patch = mapper.readValue(bookUpdateRequest, JsonPatch.class);
    var patchedNode = patch.apply(mapper.convertValue(book, JsonNode.class));
    var updatedBook = mapper.convertValue(patchedNode, Book.class);
    putBook(name, updatedBook);

    return updatedBook;
  }

  public void deleteBook(String libraryName, String id) {
    LOG.trace("Deleting book with id [{}] from library [{}]", id, libraryName);

    libraryService.getLibrary(libraryName).getItem().getBooks().removeIf(b -> id.equals(b.getId()));

    libraryStorage.deleteBook(libraryName, id);
  }

  public Book storeBook(String libraryName, MultipartFile file) throws IOException, StorageException {
    LOG.trace("Storing book from file in library with name [{}]", libraryName);

    var book = libraryStorage.store(libraryName, file);

    libraryService.getLibrary(libraryName).getItem().getBooks().add(book);

    return book;
  }

  public Book getBook(String libraryName, String id) {
    LOG.trace("Getting book with id [{}] from library [{}]", id, libraryName);

    var library = libraryService.getLibrary(libraryName).getItem();
    Optional<Book> book = library.getBooks()
            .stream()
            .filter(b -> id.equals(b.getId()))
            .findFirst();

    // TODO Find first is not necessarily safe to use if multiple books were to have the same id.

    if (book.isPresent()) {
      return book.get();
    } else {
      throw new EBookNotFoundException("Book not found");
    }
  }

  private void putBook(String libraryName, Book book) {
    LOG.trace("Putting book with id [{}] from library [{}]", book.getId(), libraryName);

    var library = libraryService.getLibrary(libraryName).getItem();

    Optional<Book> first = library.getBooks().stream().filter(b -> book.getId().equals(b.getId())).findFirst();
    if (!first.isPresent()) {
      throw new EBookNotFoundException("Missing book for put operation");
    }

    var index = library.getBooks().indexOf(first.get());
    library.getBooks().set(index, book);
  }
}
