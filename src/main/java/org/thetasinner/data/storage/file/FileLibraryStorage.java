package org.thetasinner.data.storage.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.exception.EBookDataServiceException;
import org.thetasinner.data.exception.EBookNotFoundException;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.BookMetadata;
import org.thetasinner.data.model.Library;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.StorageException;
import org.thetasinner.web.model.BookMetadataUpdateRequest;
import org.thetasinner.web.model.BookUpdateRequest;

import java.io.File;
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

public class FileLibraryStorage implements ILibraryStorage {
    private static final Logger LOG = LoggerFactory.getLogger(FileLibraryStorage.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final ReentrantLock acquireLibraryLock = new ReentrantLock();
    private final ReentrantLock createLibraryLock = new ReentrantLock();
    private String dataPath;

    @Autowired
    private FileCache<Library> cache;

    public FileLibraryStorage() {
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

    @Override
    public void load(String name) {
        try {
            acquireLibraryLock.lock();

            if (cache.has(name)) {
                throw new EBookDataServiceException("That library is already loaded");
            }

            byte[] encoded = Files.readAllBytes(Paths.get(getLibraryPath(name)));
            String result = new String(encoded, Charset.defaultCharset());

            Library library = mapper.readValue(result.getBytes(), Library.class);
            cache.put(name, library);
        }
        catch (IOException e) {
            LOG.error("Failed to load e-book library", e);
            throw new EBookDataServiceException("Failed to load e-book library", e);
        }
        finally {
            acquireLibraryLock.unlock();
        }
    }

    @Override
    public void save(String name) {
        try (FileWriter writer = new FileWriter(getLibraryPath(name))) {
            Library library = getLibrary(name);
            mapper.writeValue(writer, library);
        }
        catch (IOException e) {
            LOG.error("Failed to save e-book library", e);
            throw new EBookDataServiceException("Failed to save e-book library", e);
        }
    }

    @Override
    public void create(String name) {
        try {
            createLibraryLock.lock();

            String path = getLibraryDirectory(name);
            Path newLibraryPath = Paths.get(path);
            if (Files.exists(newLibraryPath)) {
                throw new EBookDataServiceException(String.format("Will not create new library with name [%s] because the name is already in use", name));
            }

            File dir = new File(path);
            if (!dir.mkdirs()) {
                throw new EBookDataServiceException(String.format("Unable to create directory for name [%s], check this is a valid directory name and you have permission to create it", name));
            }

            String libraryPath = getLibraryPath(name);
            Library newLibrary = new Library();
            cache.put(name, newLibrary);
            try (FileWriter writer = new FileWriter(libraryPath)) {
                mapper.writeValue(writer, newLibrary);
            }
            catch (IOException e) {
                // An IO exception here is NOT expected because the path has been checked but if it does happen then some sort
                // or recovery is needed. Can either delete the library folder or create a library validation and repair feature.
                throw new EBookDataServiceException(String.format("Failed to create library data file for new library with name [%s]", name));
            }
        }
        finally {
            createLibraryLock.unlock();
        }
    }

    @Override
    public List<Book> getBooks(String name) {
        Library library = cache.get(name);
        return library.getBooks();
    }

    @Override
    public Book createBook(String name, String url, TypedUrl.Type type) {
        Book book = new Book();
        book.setId(UUID.randomUUID().toString());
        book.setUrl(new TypedUrl(url, type));

        Library library = cache.get(name);
        library.getBooks().add(book);

        return book;
    }

    @Override
    public void store(String name, MultipartFile file) throws StorageException {
        String filename = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
        if (filename.contains("..") || filename.contains("\\") || filename.contains("/")) {
            // Security check to make sure a path embedded in the filename can't escape the storage location.
            // Or accidentally orphan itself by having a path the server can't find again.
            throw new StorageException("Won't store file which contains path component.");
        }

        UUID uuid = UUID.randomUUID();
        String storagePath = getLibraryDirectory(name) + uuid + File.separator;
        if (!new File(storagePath).mkdirs()) {
            throw new StorageException(String.format("Could not create a directory to store the new file in [%s]", filename));
        }

        String fileStoragePath = storagePath + filename;

        try {
            Files.copy(file.getInputStream(), Paths.get(fileStoragePath));
        }
        catch (IOException e) {
            throw new StorageException("Error saving the file", e);
        }

        // Once everything else is in place, add the book to the library.
        Book book = new Book();
        book.setId(uuid.toString());
        book.setUrl(new TypedUrl(fileStoragePath, TypedUrl.Type.LocalManaged));
        book.setTitle(filename);
        cache.get(name).getBooks().add(book);
    }

    @Override
    public Book updateBook(String id, String name, BookUpdateRequest bookUpdateRequest) {
        Book book = getBook(id, name);

        if (bookUpdateRequest.getTitle() != null) {
            book.setTitle(bookUpdateRequest.getTitle());
        }

        if (!CollectionUtils.isEmpty(bookUpdateRequest.getAuthors())) {
            book.getAuthors().addAll(bookUpdateRequest.getAuthors());
        }

        if (bookUpdateRequest.getPublisher() != null) {
            book.setPublisher(bookUpdateRequest.getPublisher());
        }

        if (bookUpdateRequest.getDatePublished() != null) {
            book.setDatePublished(bookUpdateRequest.getDatePublished());
        }

        if (bookUpdateRequest.getBookMetadataUpdateRequest() != null) {
            updateBookMetadata(book, bookUpdateRequest.getBookMetadataUpdateRequest());
        }

        return book;
    }

    private void updateBookMetadata(Book book, BookMetadataUpdateRequest bookMetadataUpdateRequest) {
        BookMetadata bookMetadata = book.getMetadata();
        if (bookMetadata == null) {
            bookMetadata = new BookMetadata();
            book.setMetadata(bookMetadata);
        }

        if (!CollectionUtils.isEmpty(bookMetadataUpdateRequest.getTags())) {
            if (bookMetadata.getTags() == null) {
                bookMetadata.setTags(new ArrayList<>());
            }

            bookMetadata.getTags().addAll(bookMetadataUpdateRequest.getTags());
        }

        if (bookMetadataUpdateRequest.getRating() != null) {
            bookMetadata.setRating(bookMetadataUpdateRequest.getRating());
        }
    }

    private Book getBook(String id, String name) {
        Optional<Book> book = cache.get(name).getBooks()
                .stream()
                .filter(b -> id.equals(b.getId()))
                .findFirst();

        // TODO Find first is not necessarily safe to use if multiple books were to have the same id.

        if (book.isPresent()) {
            return book.get();
        }
        else {
            throw new EBookNotFoundException("Book not found");
        }
    }

    private String getLibraryPath(String name) {
        return getLibraryDirectory(name) + "library.json";
    }

    private String getLibraryDirectory(String name) {
        return dataPath + name + File.separator;
    }

    private Library getLibrary(String name) {
        if (!cache.has(name)) {
            throw new EBookDataServiceException("That library isn't loaded");
        }

        return cache.get(name);
    }
}
