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
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.exception.EBookDataServiceException;
import org.thetasinner.data.exception.EBookFileNotFoundException;
import org.thetasinner.data.exception.InvalidRequestException;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.StorageException;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.BookUpdateRequest;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

    public List<Integer> storeAll(String token, String name, MultipartFile[] files) {
        checkCanUseLibrary(token, name);

        List<Integer> failed = new ArrayList<>();

        int storeIndex = 0;
        for (MultipartFile file : files) {
            try {
                store(name, file);
                storeIndex++;
            }
            catch (StorageException e) {
                failed.add(storeIndex);
            }
        }

        return failed;
    }

    private void store(String name, MultipartFile file) throws StorageException {
        if (file.isEmpty()) {
            throw new StorageException("File is empty");
        }

        storage.store(name, file);
    }

    public List<Book> getBooks(String token, String name) {
        checkCanUseLibrary(token, name);

        List<Book> books = storage.getBooks(name);

        return books == null ? new ArrayList<>() : books;
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

    public void deleteBook(String id, String token, String name) {
        if (StringUtils.isBlank(id)) {
            throw new InvalidRequestException("Missing request param: id");
        }

        checkCanUseLibrary(token, name);

        storage.deleteBook(id, name);
    }

    public Book updateBook(String id, String token, String name, BookUpdateRequest bookUpdateRequest) {
        if (StringUtils.isEmpty(id)) {
            throw new InvalidRequestException("Missing request param: id");
        }

        if (bookUpdateRequest == null) {
            throw new InvalidRequestException("Missing request body");
        }

        checkCanUseLibrary(token, name);

        return storage.updateBook(id, name, bookUpdateRequest);
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

    public List<String> getLibraries() {
        return storage.getLibraries();
    }
}
