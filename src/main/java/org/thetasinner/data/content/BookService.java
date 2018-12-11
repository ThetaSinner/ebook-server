package org.thetasinner.data.content;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.exception.EBookNotFoundException;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.BookMetadata;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.StorageException;
import org.thetasinner.web.model.BookMetadataUpdateRequest;
import org.thetasinner.web.model.BookUpdateRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {
  private final LibraryService libraryService;

  private final ILibraryStorage libraryStorage;

  public BookService(LibraryService libraryService, ILibraryStorage libraryStorage) {
    this.libraryService = libraryService;
    this.libraryStorage = libraryStorage;
  }

  public Book createBook(String libraryName, String url, TypedUrl.Type type) {
    Book book = new Book();
    book.setId(UUID.randomUUID().toString());
    book.setUrl(new TypedUrl(url, type));

    var library = libraryService.getLibrary(libraryName);
    library.getItem().getBooks().add(book);

    return book;
  }

  public List<Book> getBooks(String libraryName) {
    var library = libraryService.getLibrary(libraryName).getItem();
    return library.getBooks();
  }

  public Book updateBook(String id, String name, BookUpdateRequest bookUpdateRequest) {
    Book book = getBook(name, id);

    if (bookUpdateRequest.getTitle() != null) {
      book.setTitle(bookUpdateRequest.getTitle());
    }

    if (bookUpdateRequest.getIsbn() != null) {
      book.setIsbn(bookUpdateRequest.getIsbn());
    }

    if (!CollectionUtils.isEmpty(bookUpdateRequest.getAuthors())) {
      // Ensure the list of authors only contains unique values.
      book.setAuthors(
              bookUpdateRequest.getAuthors().stream().distinct().collect(Collectors.toList())
      );
    }

    if (bookUpdateRequest.getPublisher() != null) {
      book.setPublisher(bookUpdateRequest.getPublisher());
    }

    if (bookUpdateRequest.getDatePublished() != null) {
      book.setDatePublished(bookUpdateRequest.getDatePublished());
    }

    if (bookUpdateRequest.getDescription() != null) {
      book.setDescription(bookUpdateRequest.getDescription());
    }

    if (bookUpdateRequest.getBookMetadataUpdateRequest() != null) {
      updateBookMetadata(book, bookUpdateRequest.getBookMetadataUpdateRequest());
    }

    return book;
  }

  public void deleteBook(String libraryName, String id) {
    libraryService.getLibrary(libraryName).getItem().getBooks().removeIf(b -> id.equals(b.getId()));

    libraryStorage.deleteBook(libraryName, id);
  }

  public void storeBook(String libraryName, MultipartFile file) throws IOException, StorageException {
    var book = libraryStorage.store(libraryName, file);

    libraryService.getLibrary(libraryName).getItem().getBooks().add(book);
  }

  private void updateBookMetadata(Book book, BookMetadataUpdateRequest bookMetadataUpdateRequest) {
    BookMetadata bookMetadata = book.getMetadata();
    if (bookMetadata == null) {
      bookMetadata = new BookMetadata();
      book.setMetadata(bookMetadata);
    }

    if (!CollectionUtils.isEmpty(bookMetadataUpdateRequest.getTags())) {
      // Ensure the list of tags only contains unique values.
      bookMetadata.setTags(
              bookMetadataUpdateRequest.getTags().stream().distinct().collect(Collectors.toList())
      );
    }

    if (bookMetadataUpdateRequest.getRating() != null) {
      bookMetadata.setRating(bookMetadataUpdateRequest.getRating());
    }
  }

  public Book getBook(String libraryName, String id) {
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
}
