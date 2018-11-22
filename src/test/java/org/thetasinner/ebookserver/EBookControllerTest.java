package org.thetasinner.ebookserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Library;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.BookMetadataUpdateRequest;
import org.thetasinner.web.model.BookUpdateRequest;
import org.thetasinner.web.model.CommitLibrary;
import org.thetasinner.web.model.CommitRequest;
import org.thetasinner.web.model.EmptyJsonResponse;
import org.thetasinner.web.model.RequestBase;

import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Bean
    public TestRestTemplate postConstructSetup() {
        var builder = new RestTemplateBuilder()
                .requestFactory(HttpComponentsClientHttpRequestFactory.class);
        return new TestRestTemplate(builder);
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

    // TODO the image that is embedded in the pdf document is slightly different to the original so this test ends up depending on the implementation
    @Test
    public void downloadCoverForViewing() throws IOException {
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
                buildRequestUrl("/books/{id}/covers?name={name}"),
                HttpMethod.GET, entity, byte[].class, books.get(0).getId(), libraryName);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        var coverPngByteArray = FileUtils.readFileToByteArray(Paths.get(eBookDataPath, libraryName, books.get(0).getId(), "cover.png").toFile());
        assertArrayEquals(coverPngByteArray, result.getBody());
    }

    @Test
    public void createLocalUnmanagedBook() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var localFilePath = getFileSystemResourceFromClasspath("test-ebook/document.pdf").getFile().getAbsolutePath();

        var request = new RequestBase<BookAddRequest>();
        request.setName(libraryName);
        BookAddRequest bookAddRequest = new BookAddRequest();
        request.setRequest(bookAddRequest);
        bookAddRequest.setType(BookAddRequest.Type.LocalUnmanaged);
        bookAddRequest.setUrl(localFilePath);

        var result = restTemplate.postForEntity(buildRequestUrl("/books"), request, Book.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var book = result.getBody();
        assertNotNull(book);

        assertNull(book.getTitle());
        assertNotNull(book.getId());
        var url = book.getUrl();
        assertNotNull(url);
        assertEquals(TypedUrl.Type.LocalUnmanaged, url.getType());
        assertEquals(localFilePath, url.getValue());
    }

    @Test
    public void createBookForWebLink() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var webLink = "http://test.thetasinner.com/a-really-helpful-book";

        var request = new RequestBase<BookAddRequest>();
        request.setName(libraryName);
        BookAddRequest bookAddRequest = new BookAddRequest();
        request.setRequest(bookAddRequest);
        bookAddRequest.setType(BookAddRequest.Type.WebLink);
        bookAddRequest.setUrl(webLink);

        var result = restTemplate.postForEntity(buildRequestUrl("/books"), request, Book.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var book = result.getBody();
        assertNotNull(book);

        assertNull(book.getTitle());
        assertNotNull(book.getId());
        var url = book.getUrl();
        assertNotNull(url);
        assertEquals(TypedUrl.Type.WebLink, url.getType());
        assertEquals(webLink, url.getValue());
    }

    @Test
    public void createBookForUnvalidatedUrlToBook() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var bookUrl = "you don't have a copy of me or a link to me";

        var request = new RequestBase<BookAddRequest>();
        request.setName(libraryName);
        BookAddRequest bookAddRequest = new BookAddRequest();
        request.setRequest(bookAddRequest);
        bookAddRequest.setType(BookAddRequest.Type.Other);
        bookAddRequest.setUrl(bookUrl);

        var result = restTemplate.postForEntity(buildRequestUrl("/books"), request, Book.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var book = result.getBody();
        assertNotNull(book);

        assertNull(book.getTitle());
        assertNotNull(book.getId());
        var url = book.getUrl();
        assertNotNull(url);
        assertEquals(TypedUrl.Type.Other, url.getType());
        assertEquals(bookUrl, url.getValue());
    }

    @Test
    public void uploadCoverForBook() throws IOException {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var booksResponse = getBookList(libraryName);
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());
        var books = booksResponse.getBody();
        assertNotNull(books);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var body = new LinkedMultiValueMap<String, Object>();
        body.add("name", libraryName);
        body.add("cover", getFileSystemResourceFromClasspath("test-ebook/cover.png"));

        var requestEntity = new HttpEntity<>(body, headers);

        var result = restTemplate.postForEntity(buildRequestUrl("/books/{id}/covers"), requestEntity, String.class, books.get(0).getId());
        assertEquals(HttpStatus.OK, result.getStatusCode());

        var uploadedCovers = Paths.get(eBookDataPath, libraryName, books.get(0).getId()).toFile().listFiles(pathname -> pathname.getName().contains("cover-"));
        assertNotNull(uploadedCovers);
        assertEquals(1, uploadedCovers.length);

        var pattern = Pattern.compile("cover(-[a-f0-9]+)+\\.png");
        var matcher = pattern.matcher(uploadedCovers[0].getName());
        assertTrue(matcher.matches());
    }

    @Test
    public void updateAllFieldsOnBook() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = uploadBook(libraryName, "test-ebook/document.pdf");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var booksResponse = getBookList(libraryName);
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());
        var books = booksResponse.getBody();
        assertNotNull(books);

        var request = new RequestBase<BookUpdateRequest>();
        request.setName(libraryName);
        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        request.setRequest(bookUpdateRequest);

        bookUpdateRequest.setDatePublished(new Date());
        bookUpdateRequest.setDescription("An enlightening read");
        bookUpdateRequest.setIsbn("123049845903845");
        bookUpdateRequest.setPublisher("A mighty press");
        bookUpdateRequest.setTitle("Code Smells Everywhere");

        var authors = new ArrayList<String>();
        authors.add("Randy C. Markus");
        bookUpdateRequest.setAuthors(authors);

        BookMetadataUpdateRequest bookMetadataUpdateRequest = new BookMetadataUpdateRequest();
        bookUpdateRequest.setBookMetadataUpdateRequest(bookMetadataUpdateRequest);

        bookMetadataUpdateRequest.setRating((byte) 4);

        var tags = new ArrayList<String>();
        tags.add("code-quality");
        tags.add("software-development");
        tags.add("python");
        bookMetadataUpdateRequest.setTags(tags);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();

        var requestEntity = new HttpEntity<>(gson.toJson(request), headers);

        var result = restTemplate.exchange(buildRequestUrl("/books/{id}"), HttpMethod.PATCH, requestEntity, Book.class, books.get(0).getId());
        assertEquals(HttpStatus.OK, result.getStatusCode());

        var updatedBook = result.getBody();
        assertNotNull(updatedBook);

        System.out.println(updatedBook);

        assertEquals(bookUpdateRequest.getTitle(), updatedBook.getTitle());
        assertEquals(bookUpdateRequest.getDatePublished(), updatedBook.getDatePublished());
        assertEquals(bookUpdateRequest.getDescription(), updatedBook.getDescription());
        assertEquals(bookUpdateRequest.getIsbn(), updatedBook.getIsbn());
        assertEquals(bookUpdateRequest.getPublisher(), updatedBook.getPublisher());
        assertIterableEquals(bookUpdateRequest.getAuthors(), updatedBook.getAuthors());
        assertEquals(bookUpdateRequest.getBookMetadataUpdateRequest().getRating(), updatedBook.getMetadata().getRating());
        assertIterableEquals(bookUpdateRequest.getBookMetadataUpdateRequest().getTags(), updatedBook.getMetadata().getTags());
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
