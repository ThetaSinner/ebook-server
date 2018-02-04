package org.thetasinner.data.storage.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thetasinner.data.exception.EBookDataServiceException;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Library;
import org.thetasinner.data.storage.ILibraryStorage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
