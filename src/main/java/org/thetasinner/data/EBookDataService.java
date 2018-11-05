package org.thetasinner.data;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.exception.EBookDataServiceException;
import org.thetasinner.data.exception.EBookFileNotFoundException;
import org.thetasinner.data.exception.InvalidRequestException;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.StorageException;
import org.thetasinner.web.events.ChangeEventData;
import org.thetasinner.web.events.LibraryChangeService;
import org.thetasinner.web.model.*;

import javax.servlet.ServletOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.thetasinner.web.events.ChangeEventData.ChangeType.*;

@Service
public class EBookDataService {
    private static final Logger LOG = LoggerFactory.getLogger(EBookDataService.class);

    @Autowired
    private ILibraryStorage storage;

    @Autowired
    private LibraryChangeService libraryChangeService;

    public void commit(CommitRequest commitRequest) {
        boolean commitAll = Boolean.TRUE.equals(commitRequest.getCommitAll());
        boolean commitAndUnloadAll = Boolean.TRUE.equals(commitRequest.getCommitAndUnloadAll());
        if (commitAll || commitAndUnloadAll) {
            // TODO
        }
        else {
            List<CommitLibrary> commitLibraries = commitRequest.getCommitLibraries();
            if (commitLibraries == null) {
                throw new EBookDataServiceException("Cannot update no libraries");
            }
            commitLibraries.forEach(request -> {
                storage.save(request.getLibraryName());
                // TODO handle unload
            });
        }
    }

    public void createLibrary(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new EBookDataServiceException(("Provide a name"));
        }

        storage.create(name);
    }

    public List<Integer> storeAll(String name, MultipartFile[] files) {
        List<Integer> failed = new ArrayList<>();

        int storeIndex = 0;
        for (MultipartFile file : files) {
            try {
                store(name, file);
                storeIndex++;
            }
            catch (StorageException | IOException e) {
                failed.add(storeIndex);
            }
        }

        return failed;
    }

    private void store(String name, MultipartFile file) throws StorageException, IOException {
        if (file.isEmpty()) {
            throw new StorageException("File is empty");
        }

        storage.store(name, file);
    }

    public List<Book> getBooks(String name) {
        List<Book> books = storage.getBooks(name);

        return books == null ? new ArrayList<>() : books;
    }

    public Book createBook(String name, BookAddRequest bookAddRequest) {
        if (bookAddRequest == null || bookAddRequest.getUrl() == null || bookAddRequest.getType() == null) {
            throw new InvalidRequestException("Invalid add book request");
        }

        Book book = null;
        switch (bookAddRequest.getType()) {
            case LocalUnmanaged:
                Path path = Paths.get(bookAddRequest.getUrl());
                if (!Files.exists(path)) {
                    throw new EBookFileNotFoundException("Won't add book because the file does not exist");
                }

                book = storage.createBook(name, bookAddRequest.getUrl(), TypedUrl.Type.LocalUnmanaged);
                break;
            case WebLink:
                book = storage.createBook(name, bookAddRequest.getUrl(), TypedUrl.Type.WebLink);
                break;
            case Other:
                book = storage.createBook(name, bookAddRequest.getUrl(), TypedUrl.Type.Other);
                break;
        }

        // Publish book created change event.
        ChangeEventData eventData = new ChangeEventData(BookCreated, book.getId());
        libraryChangeService.publish(name, eventData);

        return book;
    }

    public void deleteBook(String id, String name) {
        if (StringUtils.isBlank(id)) {
            throw new InvalidRequestException("Missing request param: id");
        }

        storage.deleteBook(id, name);

        // Publish book deleted change event.
        ChangeEventData eventData = new ChangeEventData(BookDeleted, id);
        libraryChangeService.publish(name, eventData);
    }

    public Book updateBook(String id, String name, BookUpdateRequest bookUpdateRequest) {
        if (StringUtils.isEmpty(id)) {
            throw new InvalidRequestException("Missing request param: id");
        }

        if (bookUpdateRequest == null) {
            throw new InvalidRequestException("Missing request body");
        }

        Book book = storage.updateBook(id, name, bookUpdateRequest);

        // Publish book updated change event.
        ChangeEventData eventData = new ChangeEventData(BookUpdated, id);
        libraryChangeService.publish(name, eventData);

        return book;
    }

    public List<String> getLibraries() {
        return storage.getLibraries();
    }

    public String getBook(String id, String name, OutputStream outputStream) throws IOException {
        FileInputStream inputStream = storage.getBookInputStream(id, name);
        IOUtils.copy(inputStream, outputStream);
        return "application/pdf";
    }

    public List<Integer> uploadCover(String id, String name, MultipartFile cover) {
        if (cover == null || cover.isEmpty()) {
            return Collections.singletonList(0);
        }

        List<Integer> failedUploadIndices = new ArrayList<>(1);
        try {
            storage.storeCover(id, name, cover);
        }
        catch (StorageException e) {
            failedUploadIndices.add(0);
        }

        return failedUploadIndices;
    }
}
