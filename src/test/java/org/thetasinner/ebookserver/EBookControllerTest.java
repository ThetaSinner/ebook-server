package org.thetasinner.ebookserver;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Library;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.ebookserver.helper.EBookTestClient;
import org.thetasinner.ebookserver.helper.ResourceHelper;
import org.thetasinner.ebookserver.helper.TestDataHelper;
import org.thetasinner.ebookserver.helper.TestInfrastructureHelper;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.BookMetadataUpdateRequest;
import org.thetasinner.web.model.BookUpdateRequest;
import org.thetasinner.web.model.RequestBase;

import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private EBookTestClient eBookTestClient;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private TestDataHelper testDataHelper;

    // Should be BeforeAll, but running with SpringRunner which is using JUnit4.
    @BeforeClass
    public static void setup() throws IOException {
        TestInfrastructureHelper.cleanup();
    }

    @Test
    public void createNewLibrary() {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var path = Paths.get(TestInfrastructureHelper.ESDATA_TEST_PATH, libraryName);
        assertTrue(Files.exists(path));
    }

    @Test
    public void existingLibraryCannotBeOverwritten() {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.createLibrary(libraryName, this.port);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void libraryNameMustNotBeEmpty() {
        var response = eBookTestClient.createLibrary("", this.port);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("E-Book data service input failed validation [Library name must not be empty]", response.getBody());
    }

    @Test
    public void lookupLibraries() {
        var libraryNameBase = testDataHelper.getCurrentMethodName();
        var libraryNameOne = String.format("%s-%d", libraryNameBase, 1);
        var libraryNameTwo = String.format("%s-%d", libraryNameBase, 2);

        var response = eBookTestClient.createLibrary(libraryNameOne, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        response = eBookTestClient.createLibrary(libraryNameTwo, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var result = eBookTestClient.getLibraries(this.port);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        var libraries = result.getBody();
        assert libraries != null;
        assertTrue(2 <= libraries.size());
        assertTrue(libraries.contains(libraryNameOne));
        assertTrue(libraries.contains(libraryNameTwo));
    }

    @Test
    public void uploadToLibrary() {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // TODO another one which knows implementation details
    @Test
    public void uploadingBookWithCoverImageExtractsTheCover() {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var bookDirectories = Paths.get(TestInfrastructureHelper.ESDATA_TEST_PATH, libraryName).toFile().listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        assertNotNull(bookDirectories);
        assertEquals(1, bookDirectories.length);
        assertTrue(Paths.get(TestInfrastructureHelper.ESDATA_TEST_PATH, libraryName, bookDirectories[0].getName(), "cover.png").toFile().exists());
    }

    @Test
    public void getBooks() {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<List<Book>> libraryResponse = eBookTestClient.getBookList(libraryName, this.port);

        List<Book> books = libraryResponse.getBody();
        assertNotNull(books);
        assertEquals(2, books.size());
    }

    // TODO This test and its pair test a feature which is transparent to the user (assuming the server stays alive) but requires knowledge of the implementation to test.
    @Test
    public void libraryChangesAreNotSavedToDisk() throws IOException {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var libraryJson = FileUtils.readFileToString(Paths.get(TestInfrastructureHelper.ESDATA_TEST_PATH, libraryName, "library.json").toFile(), Charset.defaultCharset());

        var library = new Gson().fromJson(libraryJson, Library.class);
        assertTrue(library.getBooks().isEmpty());
    }

    @Test
    public void libraryChangesAreSavedToDiskWhenRequested() throws IOException {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var commitResponse = eBookTestClient.commitLibrary(libraryName, false, this.port);
        assertEquals(HttpStatus.OK, commitResponse.getStatusCode());

        var libraryJson = FileUtils.readFileToString(Paths.get(TestInfrastructureHelper.ESDATA_TEST_PATH, libraryName, "library.json").toFile(), Charset.defaultCharset());

        var library = new Gson().fromJson(libraryJson, Library.class);
        assertEquals(2, library.getBooks().size());
    }

    @Test
    public void downloadBookForReading() throws IOException {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var booksResponse = eBookTestClient.getBookList(libraryName, this.port);
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());
        var books = booksResponse.getBody();
        assertNotNull(books);

        String bookId = books.get(0).getId();
        ResponseEntity<byte[]> result = eBookTestClient.downloadBook(libraryName, bookId, this.port);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        var pdfByteArray = FileUtils.readFileToByteArray(resourceHelper.getFileSystemResourceFromClasspath("test-ebook/document.pdf").getFile());
        assertArrayEquals(pdfByteArray, result.getBody());
    }

    // TODO the image that is embedded in the pdf document is slightly different to the original so this test ends up depending on the implementation
    @Test
    public void downloadCoverForViewing() throws IOException {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var booksResponse = eBookTestClient.getBookList(libraryName, this.port);
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());
        var books = booksResponse.getBody();
        assertNotNull(books);

        String bookId = books.get(0).getId();
        ResponseEntity<byte[]> result = eBookTestClient.downloadCover(libraryName, bookId, this.port);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        var coverPngByteArray = FileUtils.readFileToByteArray(Paths.get(TestInfrastructureHelper.ESDATA_TEST_PATH, libraryName, bookId, "cover.png").toFile());
        assertArrayEquals(coverPngByteArray, result.getBody());
    }

    @Test
    public void createLocalUnmanagedBook() {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var localFilePath = resourceHelper.getFileSystemResourceFromClasspath("test-ebook/document.pdf").getFile().getAbsolutePath();

        var request = new RequestBase<BookAddRequest>();
        request.setName(libraryName);
        BookAddRequest bookAddRequest = new BookAddRequest();
        request.setRequest(bookAddRequest);
        bookAddRequest.setType(BookAddRequest.Type.LocalUnmanaged);
        bookAddRequest.setUrl(localFilePath);

        ResponseEntity<Book> result = eBookTestClient.createBook(request, this.port);
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
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var webLink = "http://test.thetasinner.com/a-really-helpful-book";

        var request = new RequestBase<BookAddRequest>();
        request.setName(libraryName);
        BookAddRequest bookAddRequest = new BookAddRequest();
        request.setRequest(bookAddRequest);
        bookAddRequest.setType(BookAddRequest.Type.WebLink);
        bookAddRequest.setUrl(webLink);

        var result = eBookTestClient.createBook(request, this.port);
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
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var bookUrl = "you don't have a copy of me or a link to me";

        var request = new RequestBase<BookAddRequest>();
        request.setName(libraryName);
        BookAddRequest bookAddRequest = new BookAddRequest();
        request.setRequest(bookAddRequest);
        bookAddRequest.setType(BookAddRequest.Type.Other);
        bookAddRequest.setUrl(bookUrl);

        var result = eBookTestClient.createBook(request, this.port);
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
    public void uploadCoverForBook() {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var booksResponse = eBookTestClient.getBookList(libraryName, this.port);
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());
        var books = booksResponse.getBody();
        assertNotNull(books);

        String bookId = books.get(0).getId();
        ResponseEntity<String> result = eBookTestClient.uploadCover(libraryName, bookId, this.port);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        var uploadedCovers = Paths.get(TestInfrastructureHelper.ESDATA_TEST_PATH, libraryName, bookId).toFile().listFiles(pathname -> pathname.getName().contains("cover-"));
        assertNotNull(uploadedCovers);
        assertEquals(1, uploadedCovers.length);

        var pattern = Pattern.compile("cover(-[a-f0-9]+)+\\.png");
        var matcher = pattern.matcher(uploadedCovers[0].getName());
        assertTrue(matcher.matches());
    }

    @Test
    public void updateAllFieldsOnBook() {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var booksResponse = eBookTestClient.getBookList(libraryName, this.port);
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

        String bookId = books.get(0).getId();
        ResponseEntity<Book> result = eBookTestClient.updateBook(request, bookId, this.port);
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

    @Test
    public void deleteBook() {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var booksResponse = eBookTestClient.getBookList(libraryName, this.port);
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());

        var books = booksResponse.getBody();
        assertNotNull(books);
        assertEquals(1, books.size());

        String bookId = books.get(0).getId();
        eBookTestClient.deleteBook(libraryName, bookId, this.port);

        booksResponse = eBookTestClient.getBookList(libraryName, this.port);
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());

        books = booksResponse.getBody();
        assertNotNull(books);
        assertEquals(0, books.size());
    }

    @Test
    public void libraryCanBeUnloadedOnCommitThenLoadedWhenRequired() throws IOException {
        var libraryName = testDataHelper.getCurrentMethodName();
        var response = eBookTestClient.createLibrary(libraryName, this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var commitResponse = eBookTestClient.commitLibrary(libraryName, true, this.port);
        assertEquals(HttpStatus.OK, commitResponse.getStatusCode());

        var libraryJson = FileUtils.readFileToString(Paths.get(TestInfrastructureHelper.ESDATA_TEST_PATH, libraryName, "library.json").toFile(), Charset.defaultCharset());

        var library = new Gson().fromJson(libraryJson, Library.class);
        assertEquals(1, library.getBooks().size());
        assertEquals("document.pdf", library.getBooks().get(0).getTitle());
        String testTitle = "Test title";
        library.getBooks().get(0).setTitle(testTitle);
        FileUtils.writeStringToFile(Paths.get(TestInfrastructureHelper.ESDATA_TEST_PATH, libraryName, "library.json").toFile(), new Gson().toJson(library), Charset.defaultCharset());

        var booksResponse = eBookTestClient.getBookList(libraryName, this.port);
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());

        var books = booksResponse.getBody();
        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals(testTitle, books.get(0).getTitle());
    }
}
