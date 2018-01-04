package org.thetasinner.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Library;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.BookAddResponse;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class EBookDataService {
    private static final Logger LOG = LoggerFactory.getLogger(EBookDataService.class);

    private Library library;

    public boolean load(String name) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(name + ".json"));
            String result = new String(encoded, Charset.defaultCharset());

            ObjectMapper mapper = new ObjectMapper();
            library = mapper.readValue(result.getBytes(), Library.class);
        } catch (IOException e) {
            LOG.error("Failed to load e-book library", e);
            return false;
        }

        return true;
    }

    public boolean save(String name) {
        try (FileWriter writer = new FileWriter(name + ".json")) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, library);
        } catch (IOException e) {
            LOG.error("Failed to save e-book library", e);
            return false;
        }

        return true;
    }

    public List<Book> getBooks() {
        if (library != null) {
            return library.getBooks();
        }

        return new ArrayList<>();
    }

    public BookAddResponse addBook(BookAddRequest bookAddRequest) {
        if (bookAddRequest == null || bookAddRequest.getPath() == null) {
            return new BookAddResponse(BookAddResponse.ResponseStatus.INVALID_PAYLOAD);
        }

        Path path = Paths.get(bookAddRequest.getPath());
        if (!Files.exists(path)) {
            return new BookAddResponse(BookAddResponse.ResponseStatus.FILE_NOT_FOUND);
        }

        Book book = new Book();
        book.setTitle(path.getFileName().toString());

        library.getBooks().add(book);

        return new BookAddResponse(BookAddResponse.ResponseStatus.SUCCESS);
    }
}
