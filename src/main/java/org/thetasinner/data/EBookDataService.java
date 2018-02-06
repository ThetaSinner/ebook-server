package org.thetasinner.data;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thetasinner.data.exception.EBookDataServiceException;
import org.thetasinner.data.exception.EBookFileNotFoundException;
import org.thetasinner.data.exception.InvalidRequestException;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.BookMetadata;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.BookMetadataUpdateRequest;
import org.thetasinner.web.model.BookUpdateRequest;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.thetasinner.web.model.BookAddRequest.Type.LocalUnmanaged;

@Service
public class EBookDataService {
    private static final Logger LOG = LoggerFactory.getLogger(EBookDataService.class);

    @Autowired
    private ILibraryStorage storage;

    public String load(String token, String name) {
        if (StringUtils.isEmpty(name)) {
            throw new EBookDataServiceException("Provide a name");
        }

        String updatedToken = createOrUpdateToken(token, name);
        storage.load(name);
        return updatedToken;
    }

    public void save(String token, String name) {
        checkCanUseLibrary(token, name);

        storage.save(name);
    }

    public String create(String token, String name) {
        if (StringUtils.isEmpty(name)) {
            throw new EBookDataServiceException(("Provide a name"));
        }

        String updatedToken = createOrUpdateToken(token, name);
        storage.create(name);
        return updatedToken;
    }

    public List<Book> getBooks(String token, String name) {
        checkCanUseLibrary(token, name);

        List<Book> books = storage.getBooks(name);

        return books == null ? new ArrayList<>() : books;
    }

    public Book getBook(String id) {
        /*if (id == null) {
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
        }*/
        throw new EBookDataServiceException("not implemented");
    }

    public Book createBook(String token, String name, BookAddRequest bookAddRequest) {
        checkCanUseLibrary(token, name);

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

        return book;
    }

    public BookMetadata getBookMetadata(String id) {
        /*if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        Book book = getBook(id);

        if (book.getMetadata() != null) {
            return book.getMetadata();
        }
        else {
            throw new MetadataNotFoundException("Book does not have metadata");
        }*/
        throw new EBookDataServiceException("not implemented");
    }

    public BookMetadata createBookMetadata(String id, BookMetadata metadata) {
        /*if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        Book book = getBook(id);

        if (book.getMetadata() == null) {
            book.setMetadata(metadata);
            return metadata;
        }
        else {
            throw new EBookDataServiceException("Won't create metadata because this book already has metadata");
        }*/
        throw new EBookDataServiceException("not implemented");
    }

    public void deleteBook(String id) {
        /*if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        library.getBooks().removeIf(b -> id.equals(b.getId()));*/
        throw new EBookDataServiceException("not implemented");
    }

    public void deleteBookMetadata(String id) {
        /*if (id == null) {
            throw new InvalidRequestException("Missing request param: id");
        }

        Book book = getBook(id);

        book.setMetadata(null);*/
        throw new EBookDataServiceException("not implemented");
    }

    public Book updateBook(String id, BookUpdateRequest bookUpdateRequest) {
        /*if (id == null) {
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

        return book;*/
        throw new EBookDataServiceException("not implemented");
    }

    public BookMetadata updateBookMetadata(String id, BookMetadataUpdateRequest bookMetadataUpdateRequest) {
        /*if (id == null) {
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

        return bookMetadata;*/
        throw new EBookDataServiceException("not implemented");
    }

    private String createOrUpdateToken(String token, String name) {
        String newToken;
        try {
            if (StringUtils.isEmpty(token)) {
                // TODO extract all of this and create a property.
                Algorithm algorithm = Algorithm.HMAC256("mysecret");
                newToken = JWT.create()
                        .withIssuer("ebook-server")
                        .withArrayClaim("libraries", new String[]{name})
                        .sign(algorithm);
            }
            else {
                List<String> claimedLibraries = getClaimedLibraries(token);
                claimedLibraries.add(name);

                Algorithm algorithm = Algorithm.HMAC256("mysecret");
                newToken = JWT.create()
                        .withIssuer("ebook-server")
                        .withArrayClaim("libraries", claimedLibraries.toArray(new String[claimedLibraries.size()]))
                        .sign(algorithm);
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new EBookDataServiceException("Error creating token");
        }

        return newToken;
    }

    private void checkCanUseLibrary(String token, String name) {
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(name)) {
            throw new EBookDataServiceException("Provide name and token");
        }

        List<String> claimedLibraries = getClaimedLibraries(token);
        if (!claimedLibraries.contains(name)) {
            throw new EBookDataServiceException("Unauthorized library access");
        }
    }

    private List<String> getClaimedLibraries(String token) {
        if (StringUtils.isEmpty(token)) {
            return new ArrayList<>();
        }

        DecodedJWT decoded = JWT.decode(token);

        String issuer = decoded.getIssuer();
        if (!"ebook-server".equals(issuer)) {
            throw new EBookDataServiceException("Invalid token");
        }

        Claim librariesClaim = decoded.getClaim("libraries");
        if (librariesClaim.isNull()) {
            throw new EBookDataServiceException("Invalid token");
        }

        return librariesClaim.asList(String.class);
    }
}
