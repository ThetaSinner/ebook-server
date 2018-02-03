package org.thetasinner.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thetasinner.data.exception.*;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.BookMetadata;
import org.thetasinner.data.model.Library;
import org.thetasinner.web.model.BookAddRequest;
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

@Service
public class EBookDataService {
    private static final Logger LOG = LoggerFactory.getLogger(EBookDataService.class);

    @Value("${es.data.path:esdata}")
    private String dataPath;

    private Library library;

    private static final ObjectMapper mapper = new ObjectMapper();

    public EBookDataService() {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void load(String name) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(dataPath + File.separator + name + ".json"));
            String result = new String(encoded, Charset.defaultCharset());

            library = mapper.readValue(result.getBytes(), Library.class);
        } catch (IOException e) {
            LOG.error("Failed to load e-book library", e);
            throw new EBookDataServiceException("Failed to load e-book library", e);
        }
    }

    public void save(String name) {
        try (FileWriter writer = new FileWriter(name + ".json")) {
            mapper.writeValue(writer, library);
        } catch (IOException e) {
            LOG.error("Failed to save e-book library", e);
            throw new EBookDataServiceException("Failed to save e-book library", e);
        }
    }

    public List<Book> getBooks() {
        if (library == null || CollectionUtils.isEmpty(library.getBooks())) {
            return new ArrayList<>();
        }

        return library.getBooks();
    }

    public Book getBook(String id) {
        if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        Optional<Book> book = library.getBooks()
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

    public Book createBook(BookAddRequest bookAddRequest) {
        if (bookAddRequest == null || bookAddRequest.getPath() == null) {
            throw new InvalidRequestException("Invalid add book request");
        }

        Path path = Paths.get(bookAddRequest.getPath());
        if (!Files.exists(path)) {
            throw new EBookFileNotFoundException("Won't add book because the file does not exist");
        }

        Book book = new Book();
        book.setTitle(path.getFileName().toString());

        library.getBooks().add(book);

        return book;
    }

    public BookMetadata getBookMetadata(String id) {
        if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        Book book = getBook(id);

        if (book.getMetadata() != null) {
            return book.getMetadata();
        }
        else {
            throw new MetadataNotFoundException("Book does not have metadata");
        }
    }

    public BookMetadata createBookMetadata(String id, BookMetadata metadata) {
        if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        Book book = getBook(id);

        if (book.getMetadata() == null) {
            book.setMetadata(metadata);
            return metadata;
        }
        else {
            throw new EBookDataServiceException("Won't create metadata because this book already has metadata");
        }
    }

    public void deleteBook(String id) {
        if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        library.getBooks().removeIf(b -> id.equals(b.getId()));
    }

    public void deleteBookMetadata(String id) {
        if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        Book book = getBook(id);

        book.setMetadata(null);
    }

    public Book updateBook(String id, BookUpdateRequest bookUpdateRequest) {
        if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        if (bookUpdateRequest == null) {
            throw new InvalidRequestException("Missing request body");
        }

        Book book = getBook(id);

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
            // TODO this causes an extra scan to find the book
            updateBookMetadata(id, bookUpdateRequest.getBookMetadataUpdateRequest());
        }

        return book;
    }

    public BookMetadata updateBookMetadata(String id, BookMetadataUpdateRequest bookMetadataUpdateRequest) {
        if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        if (bookMetadataUpdateRequest == null) {
            throw new InvalidRequestException("Missing request body");
        }

        BookMetadata bookMetadata = getBookMetadata(id);

        if (!CollectionUtils.isEmpty(bookMetadataUpdateRequest.getTags())) {
            bookMetadata.getTags().addAll(bookMetadataUpdateRequest.getTags());
        }

        if (bookMetadataUpdateRequest.getRating() != null) {
            bookMetadata.setRating(bookMetadataUpdateRequest.getRating());
        }

        return bookMetadata;
    }
}
