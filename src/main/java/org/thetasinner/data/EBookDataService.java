package org.thetasinner.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thetasinner.data.exception.*;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.BookMetadata;
import org.thetasinner.data.model.Library;
import org.thetasinner.web.model.BookAddRequest;

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

    private Library library;

    private static final ObjectMapper mapper = new ObjectMapper();

    public EBookDataService() {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void load(String name) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(name + ".json"));
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
        if (library != null) {
            return library.getBooks();
        }

        return new ArrayList<>();
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

    public Book addBook(BookAddRequest bookAddRequest) {
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
}
