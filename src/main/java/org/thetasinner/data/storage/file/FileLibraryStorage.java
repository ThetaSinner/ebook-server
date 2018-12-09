package org.thetasinner.data.storage.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.exception.EBookDataServiceException;
import org.thetasinner.data.exception.EBookNotFoundException;
import org.thetasinner.data.image.ExtractionProperties;
import org.thetasinner.data.image.PdfImageExtractor;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.BookMetadata;
import org.thetasinner.data.model.Library;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.StorageException;
import org.thetasinner.web.model.BookMetadataUpdateRequest;
import org.thetasinner.web.model.BookUpdateRequest;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class FileLibraryStorage implements ILibraryStorage {
  private static final Logger LOG = LoggerFactory.getLogger(FileLibraryStorage.class);

  private final ObjectMapper mapper = new ObjectMapper();
  private final ReentrantLock acquireLibraryLock = new ReentrantLock();
  private final ReentrantLock createLibraryLock = new ReentrantLock();
  private String dataPath;

  private static final String COVER_FILE = "cover.png";

  private FileCache<Library> cache;

  public FileLibraryStorage(FileCache<Library> cache) {
    this.cache = cache;

    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  @Value("${es.data.path:esdata}")
  private void setDataPath(String param) {
    if (param.charAt(param.length() - 1) != File.separatorChar) {
      param += File.separator;
    }
    dataPath = param;

    if (!Files.exists(Paths.get(dataPath))) {
      File f = new File(dataPath);
      if (!f.mkdirs()) {
        throw new IllegalStateException("The directory specified by es.data.path does not exist and cannot be created");
      }
    }
  }

  private void load(String name) {
    try {
      acquireLibraryLock.lock();

      if (cache.has(name)) {
        return;
      }

      var library = loadLibraryFromFile(name);
      cache.put(name, library);
    } catch (IOException e) {
      var msg = "Failed to load e-book library";
      LOG.error(msg, e);
      throw new EBookDataServiceException(msg, e);
    } finally {
      acquireLibraryLock.unlock();
    }
  }

  private Library loadLibraryFromFile(String libraryName) throws IOException {
    var libraryData = Files.readString(Paths.get(getLibraryPath(libraryName)));
    return mapper.readValue(libraryData, Library.class);
  }

  @Override
  public void save(String libraryName, Boolean unload) {
    // Ignore save requests for libraries which are not loaded.
    if (!cache.has(libraryName)) return;

    try (var writer = new FileWriter(getLibraryPath(libraryName), Charset.forName("UTF-8"))) {
      saveLibrary(libraryName, writer);

      if (unload) {
        cache.remove(libraryName);
      }
    } catch (IOException e) {
      var msg = "Failed to save e-book library";
      LOG.error(msg, e);
      throw new EBookDataServiceException(msg, e);
    }
  }

  private void saveLibrary(String name, FileWriter writer) throws IOException {
    var library = getLibrary(name).getItem();
    mapper.writeValue(writer, library);
  }

  @Override
  public void create(String libraryName) {
    try {
      createLibraryLock.lock();

      createDirectoryForLibrary(libraryName);
      createLibrary(libraryName);
    } finally {
      createLibraryLock.unlock();
    }
  }

  private void createLibrary(String libraryName) {
    Library newLibrary = new Library();
    cache.put(libraryName, newLibrary);
    save(libraryName, false);
  }

  private void createDirectoryForLibrary(String libraryName) {
    var libraryDirectory = getLibraryDirectory(libraryName);
    checkLibraryDirectoryNotAlreadyInUse(libraryDirectory, libraryName);
    createLibraryDirectory(libraryDirectory, libraryName);
  }

  private void createLibraryDirectory(String libraryDirectory, String libraryName) {
    var dir = new File(libraryDirectory);
    if (!dir.mkdirs()) {
      throw new EBookDataServiceException(String.format("Unable to create library directory for name [%s], " +
              "check this is a valid directory name and you have permission to createLibrary it", libraryName));
    }
  }

  private void checkLibraryDirectoryNotAlreadyInUse(String libraryDirectory, String libraryName) {
    Path libraryPath = Paths.get(libraryDirectory);
    if (Files.exists(libraryPath)) {
      throw new EBookDataServiceException(String.format("Will not create library new library with name [%s] because the name is already in use", libraryName));
    }
  }

  @Override
  public List<Book> getBooks(String name) {
    var library = getLibrary(name).getItem();
    return library.getBooks();
  }

  @Override
  public Book createBook(String name, String url, TypedUrl.Type type) {
    Book book = new Book();
    book.setId(UUID.randomUUID().toString());
    book.setUrl(new TypedUrl(url, type));

    var library = getLibrary(name);
    library.getItem().getBooks().add(book);

    return book;
  }

  @Override
  public void store(String name, MultipartFile file) throws StorageException, IOException {
    var fileName = validateUploadFile(file);

    UUID uuid = UUID.randomUUID();
    String storagePath = getLibraryDirectory(name) + uuid + File.separator;
    if (!new File(storagePath).mkdirs()) {
      throw new StorageException(String.format("Could not createLibrary a directory to store the new file in [%s]", fileName));
    }

    String fileStoragePath = storagePath + fileName;

    try {
      Files.copy(file.getInputStream(), Paths.get(fileStoragePath));
    } catch (IOException e) {
      throw new StorageException("Error saving the file", e);
    }

    extractAndStoreCover(storagePath, fileStoragePath);

    // Once everything else is in place, add the book to the library.
    Book book = new Book();
    book.setId(uuid.toString());
    book.setUrl(new TypedUrl(fileStoragePath, TypedUrl.Type.LocalManaged));
    book.setTitle(fileName);
    getLibrary(name).getItem().getBooks().add(book);
  }

  private void extractAndStoreCover(String storagePath, String fileStoragePath) throws IOException {
    var images = PdfImageExtractor.extract(new File(fileStoragePath), new ExtractionProperties());
    if (images.size() == 0) {
      return;
    }

    var outPath = Paths.get(storagePath, COVER_FILE);

    var im = images.get(0);
    ImageIO.write(im, "PNG", outPath.toFile());
  }

  @Override
  public Book updateBook(String id, String name, BookUpdateRequest bookUpdateRequest) {
    var library = getLibrary(name);
    Book book = getBook(library.getItem(), id);

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

  @Override
  public void deleteBook(String id, String name) {
    // TODO If this entry was a file then it should be deleted here.
    getLibrary(name).getItem().getBooks().removeIf(b -> id.equals(b.getId()));
  }

  @Override
  public List<String> getLibraries() {
    List<String> libraries = new ArrayList<>();
    String libraryDirectory = getLibraryDirectory("");
    try (var paths = Files.newDirectoryStream(Paths.get(libraryDirectory))) {
      for (var path : paths) {
        var filePath = path.getFileName();
        if (filePath == null) continue;
        libraries.add(filePath.toString());
      }
    } catch (IOException e) {
      throw new EBookDataServiceException("Failed to get a list of library directories", e);
    }

    return libraries;
  }

  @Override
  public FileInputStream getBookInputStream(String id, String name) throws FileNotFoundException {
    var library = getLibrary(name);
    Book book = getBook(library.getItem(), id);

    TypedUrl typedUrl = book.getUrl();
    if (typedUrl.getType() != TypedUrl.Type.LocalManaged && typedUrl.getType() != TypedUrl.Type.LocalUnmanaged) {
      throw new EBookDataServiceException("Not a local book");
    }

    File file = new File(typedUrl.getValue());
    if (!file.canRead()) {
      throw new EBookDataServiceException("File is not accessible");
    }

    return new FileInputStream(file);
  }

  @Override
  public void storeCover(String id, String name, MultipartFile cover) throws StorageException {
    var originalFileName = validateUploadFile(cover);

    int lastDot = originalFileName.lastIndexOf('.');
    String fileName = "cover-" + UUID.randomUUID().toString() + originalFileName.substring(lastDot);

    var library = getLibrary(name);
    Book theBook = getBook(library.getItem(), id);
    Path path = Paths.get(theBook.getUrl().getValue());

    var storageFolder = path.getParent();
    if (storageFolder == null) {
      return;
    }
    Path savePath = Paths.get(storageFolder.toString(), fileName);

    try {
      Files.copy(cover.getInputStream(), savePath);
    } catch (IOException e) {
      throw new StorageException("Error saving the file", e);
    }

    theBook.getCovers().add(new TypedUrl(savePath.toString(), TypedUrl.Type.LocalManaged));
  }

  private String validateUploadFile(MultipartFile file) throws StorageException {
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
      throw new StorageException("Cannot get original filename from uploaded file.");
    }

    String originalFileName = org.springframework.util.StringUtils.cleanPath(originalFilename);
    if (originalFileName.contains("..") || originalFileName.contains("\\") || originalFileName.contains("/")) {
      // Security check to make sure a path embedded in the filename can't escape the storage location.
      // Or accidentally orphan itself by having a path the server can't find again.
      throw new StorageException("Won't store file which contains path component.");
    }

    return originalFilename;
  }

  @Override
  public FileInputStream getCoverInputStream(String bookId, String libraryName) throws FileNotFoundException {
    var library = getLibrary(libraryName);
    Book book = getBook(library.getItem(), bookId);

    TypedUrl typedUrl = book.getUrl();
    if (typedUrl.getType() != TypedUrl.Type.LocalManaged) {
      throw new EBookDataServiceException("Can only get cover for local managed book");
    }

    var bookPath = Paths.get(typedUrl.getValue());
    var bookFolder = bookPath.getParent();
    if (bookFolder == null) {
      return null;
    }
    File file = Paths.get(bookFolder.toString(), COVER_FILE).toFile();
    if (!file.canRead()) {
      throw new EBookDataServiceException("Cover file is not accessible or does not exist");
    }

    return new FileInputStream(file);
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

  private Book getBook(Library library, String id) {
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

  private String getLibraryPath(String name) {
    return getLibraryDirectory(name) + "library.json";
  }

  private String getLibraryDirectory(String name) {
    return dataPath + name + File.separator;
  }

  private LockableItem<Library> getLibrary(String name) {
    if (!cache.has(name)) {
      load(name);
    }

    return cache.get(name);
  }
}
