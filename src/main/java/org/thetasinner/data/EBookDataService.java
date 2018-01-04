package org.thetasinner.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Library;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
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

    public List<Book> getBooks() {
        if (library != null) {
            return library.getBooks();
        }

        return new ArrayList<>();
    }
}
