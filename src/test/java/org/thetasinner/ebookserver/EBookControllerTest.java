package org.thetasinner.ebookserver;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Library;
import org.thetasinner.web.model.CommitLibrary;
import org.thetasinner.web.model.CommitRequest;
import org.thetasinner.web.model.EmptyJsonResponse;

import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EBookControllerTest {
    @LocalServerPort
    private int port;

    @Value("${es.data.path}")
    private String eBookDataPath;

    @Autowired
    private TestRestTemplate restTemplate;

    // Should be BeforeAll, but running with SpringRunner which is using JUnit4.
    @BeforeClass
    public static void setup() throws IOException {
        cleanup();
    }

    @Test
    public void createNewLibrary() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var path = Paths.get(eBookDataPath, libraryName);
        assertTrue(Files.exists(path));
    }

    @Test
    public void existingLibraryCannotBeOverwritten() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = createProject(libraryName);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void libraryNameMustNotBeEmpty() {
        var response = createProject("");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("E-Book data service input failed validation [Library name must not be empty]", response.getBody());
    }

    @Test
    public void lookupLibraries() {
        var libraryNameBase = getCurrentMethodName();
        var libraryNameOne = String.format("%s-%d", libraryNameBase, 1);
        var libraryNameTwo = String.format("%s-%d", libraryNameBase, 2);

        var response = createProject(libraryNameOne);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        response = createProject(libraryNameTwo);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var result = getLibraries();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        var libraries = result.getBody();
        assert libraries != null;
        assertTrue(2 <= libraries.size());
        assertTrue(libraries.contains(libraryNameOne));
        assertTrue(libraries.contains(libraryNameTwo));
    }

    @Test
    public void uploadToLibrary() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // TODO another one which knows implementation details
    @Test
    public void uploadingBookWithCoverImageExtractsTheCover() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var bookDirectories = Paths.get(eBookDataPath, libraryName).toFile().listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        assertNotNull(bookDirectories);
        assertEquals(1, bookDirectories.length);
        assertTrue(Paths.get(eBookDataPath, libraryName, bookDirectories[0].getName(), "cover.png").toFile().exists());
    }

    @Test
    public void getBooks() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<List<Book>> libraryResponse = getBookList(libraryName);

        List<Book> books = libraryResponse.getBody();
        assertNotNull(books);
        assertEquals(2, books.size());
    }

    private ResponseEntity<List<Book>> getBookList(String libraryName) {
        var uriParams = new HashMap<String, String>();
        uriParams.put("name", libraryName);
        return restTemplate.exchange(
                buildRequestUrl("/books?name={name}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>(){},
                uriParams
        );
    }

    // TODO This test and its pair test a feature which is transparent to the user (assuming the server stays alive) but requires knowledge of the implementation to test.
    @Test
    public void libraryChangesAreNotSavedToDisk() throws IOException {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var libraryJson = FileUtils.readFileToString(Paths.get(eBookDataPath, libraryName, "library.json").toFile(), Charset.defaultCharset());

        var library = new Gson().fromJson(libraryJson, Library.class);
        assertTrue(library.getBooks().isEmpty());
    }

    @Test
    public void libraryChangesAreSavedToDiskWhenRequested() throws IOException {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var commitRequest = new CommitRequest();
        CommitLibrary commitLibrary = new CommitLibrary();
        commitLibrary.setLibraryName(libraryName);
        // TODO fix this hideous mess please!
        commitRequest.setCommitLibraries(new ArrayList<>());
        commitRequest.getCommitLibraries().add(commitLibrary);
        var commitResponse = restTemplate.postForEntity(buildRequestUrl("/libraries/commit"), commitRequest, EmptyJsonResponse.class);
        assertEquals(HttpStatus.OK, commitResponse.getStatusCode());

        var libraryJson = FileUtils.readFileToString(Paths.get(eBookDataPath, libraryName, "library.json").toFile(), Charset.defaultCharset());

        var library = new Gson().fromJson(libraryJson, Library.class);
        assertEquals(2, library.getBooks().size());
    }

    @Test
    public void downloadBookForReading() throws IOException {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var booksResponse = getBookList(libraryName);
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());
        var books = booksResponse.getBody();
        assertNotNull(books);

        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        var entity = new HttpEntity<>(headers);

        var result = restTemplate.exchange(
                buildRequestUrl("/books/any-title-i-like?id={id}&name={name}"),
                HttpMethod.GET, entity, byte[].class, books.get(0).getId(), libraryName);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        var pdfByteArray = FileUtils.readFileToByteArray(getFileSystemResourceFromClasspath("test-ebook/document.pdf").getFile());
        assertArrayEquals(pdfByteArray, result.getBody());
    }

    private ResponseEntity<String> uploadBook(String libraryName, String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var body = new LinkedMultiValueMap<String, Object>();
        body.add("name", libraryName);
        body.add("files", getFileSystemResourceFromClasspath(name));

        var requestEntity = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(buildRequestUrl("/libraries/upload"), requestEntity, String.class);
    }

    private FileSystemResource getFileSystemResourceFromClasspath(String name) {
        var resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource(name);
        assertNotNull(resource);
        return new FileSystemResource(resource.getFile());
    }

    private ResponseEntity<List> getLibraries() {
        return restTemplate.getForEntity(buildRequestUrl("/libraries"), List.class,  new EmptyRequest());
    }

    private ResponseEntity<String> createProject(String libraryName) {
        var uriParams = new HashMap<String, String>();
        uriParams.put("name", libraryName);

        return restTemplate.postForEntity(buildRequestUrl("/libraries?name={name}"), new EmptyRequest(), String.class, uriParams);
    }

    private String getCurrentMethodName() {
        // Indexing to 2 removes 'getStackTrace' and the current method name 'getCurrentMethodName'
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    private String buildRequestUrl(String uri) {
        return String.format("http://localhost:%d%s", port, uri);
    }

    private static void cleanup() throws IOException {
        // MUST match the value in the application properties (both required).
        // See https://github.com/junit-team/junit5/issues/419
        var testLibraryPath = Paths.get("esdata-test");
        if (Files.exists(testLibraryPath)){
            FileUtils.deleteDirectory(testLibraryPath.toFile());
        }
    }
}
