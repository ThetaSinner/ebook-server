package org.thetasinner.ebookserver;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.BookMetadata;
import org.thetasinner.data.model.Library;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.ebookserver.helper.EBookTestClient;
import org.thetasinner.ebookserver.helper.PatchHelper;
import org.thetasinner.ebookserver.helper.ResourceHelper;
import org.thetasinner.ebookserver.helper.TestDataHelper;
import org.thetasinner.ebookserver.helper.TestInfrastructureHelper;
import org.thetasinner.web.model.BookAddRequest;
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
@TestPropertySource("classpath:application-ebook.properties")
public class EBookControllerTest {
  private static final String ESDATA_TEST_PATH = "esdata-test";

  @LocalServerPort
  private int port;

  @Autowired
  private EBookTestClient eBookTestClient;

  @Autowired
  private ResourceHelper resourceHelper;

  @Autowired
  private TestDataHelper testDataHelper;

  @Autowired
  private PatchHelper patchHelper;

  // Should be BeforeAll, but running with SpringRunner which is using JUnit4.
  @BeforeClass
  public static void setup() throws IOException {
    TestInfrastructureHelper.cleanup(ESDATA_TEST_PATH);
  }

  @Test
  public void createNewLibrary() {
    var libraryName = testDataHelper.getCurrentMethodName();
    var response = eBookTestClient.createLibrary(libraryName, this.port);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    var path = Paths.get(ESDATA_TEST_PATH, libraryName);
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

    var bookDirectories = Paths.get(ESDATA_TEST_PATH, libraryName).toFile().listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
    assertNotNull(bookDirectories);
    assertEquals(1, bookDirectories.length);
    assertTrue(Paths.get(ESDATA_TEST_PATH, libraryName, bookDirectories[0].getName(), "cover.png").toFile().exists());
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

    var libraryJson = FileUtils.readFileToString(Paths.get(ESDATA_TEST_PATH, libraryName, "library.json").toFile(), Charset.defaultCharset());

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

    var libraryJson = FileUtils.readFileToString(Paths.get(ESDATA_TEST_PATH, libraryName, "library.json").toFile(), Charset.defaultCharset());

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

    var coverPngByteArray = FileUtils.readFileToByteArray(Paths.get(ESDATA_TEST_PATH, libraryName, bookId, "cover.png").toFile());
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

    var uploadedCovers = Paths.get(ESDATA_TEST_PATH, libraryName, bookId).toFile().listFiles(pathname -> pathname.getName().contains("cover-"));
    assertNotNull(uploadedCovers);
    assertEquals(1, uploadedCovers.length);

    var pattern = Pattern.compile("cover(-[a-f0-9]+)+\\.png");
    var matcher = pattern.matcher(uploadedCovers[0].getName());
    assertTrue(matcher.matches());
  }

  @Test
  public void updateAllFieldsOnBook() throws IOException {
    var libraryName = testDataHelper.getCurrentMethodName();
    var response = eBookTestClient.createLibrary(libraryName, this.port);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    response = eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", this.port);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    var booksResponse = eBookTestClient.getBookList(libraryName, this.port);
    assertEquals(HttpStatus.OK, booksResponse.getStatusCode());
    var books = booksResponse.getBody();
    assertNotNull(books);

    var originalBook = books.get(0);
    assertNotNull(originalBook);

    var modifiedBook = SerializationUtils.clone(originalBook);
    modifiedBook.setDatePublished(new Date());
    modifiedBook.setDescription("An enlightening read");
    modifiedBook.setIsbn("123049845903845");
    modifiedBook.setPublisher("A mighty press");
    modifiedBook.setTitle("Code Smells Everywhere");

    var authors = new ArrayList<String>();
    authors.add("Randy C. Markus");
    modifiedBook.setAuthors(authors);

    BookMetadata metadata = new BookMetadata();
    modifiedBook.setMetadata(metadata);
    metadata.setRating((byte) 4);

    var tags = new ArrayList<String>();
    tags.add("code-quality");
    tags.add("software-development");
    tags.add("python");
    metadata.setTags(tags);

    var patch = patchHelper.getJsonPatch(originalBook, modifiedBook);

    String bookId = originalBook.getId();
    ResponseEntity<Book> result = eBookTestClient.updateBook(patch, bookId, libraryName, this.port);
    assertEquals(HttpStatus.OK, result.getStatusCode());

    var updatedBook = result.getBody();
    assertNotNull(updatedBook);

    assertEquals(modifiedBook.getTitle(), updatedBook.getTitle());
    assertEquals(modifiedBook.getDatePublished(), updatedBook.getDatePublished());
    assertEquals(modifiedBook.getDescription(), updatedBook.getDescription());
    assertEquals(modifiedBook.getIsbn(), updatedBook.getIsbn());
    assertEquals(modifiedBook.getPublisher(), updatedBook.getPublisher());
    assertIterableEquals(modifiedBook.getAuthors(), updatedBook.getAuthors());
    assertEquals(modifiedBook.getMetadata().getRating(), updatedBook.getMetadata().getRating());
    assertIterableEquals(modifiedBook.getMetadata().getTags(), updatedBook.getMetadata().getTags());
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

    String testTitle = "Test title";
    modifyLibraryOnDisk(libraryName, "document.pdf", testTitle);

    checkBookTitle(libraryName, testTitle);
  }

  @Test
  public void saveAndUnloadAllLibraries() throws IOException {
    var libraryNameBase = testDataHelper.getCurrentMethodName();
    var libraryNameOne = libraryNameBase + "-one";
    var libraryNameTwo = libraryNameBase + "-two";
    var response = eBookTestClient.createLibrary(libraryNameOne, this.port);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    response = eBookTestClient.createLibrary(libraryNameTwo, this.port);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    response = eBookTestClient.uploadBook(libraryNameOne, "test-ebook/document.pdf", this.port);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    response = eBookTestClient.uploadBook(libraryNameTwo, "test-ebook/document.pdf", this.port);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    var commitResponse = eBookTestClient.commitAllLibraries(port);
    assertEquals(HttpStatus.OK, commitResponse.getStatusCode());

    var testTitleOne = "Test title 1";
    modifyLibraryOnDisk(libraryNameOne, "document.pdf", testTitleOne);

    var testTitleTwo = "Test title 2";
    modifyLibraryOnDisk(libraryNameTwo, "document.pdf", testTitleTwo);

    checkBookTitle(libraryNameOne, testTitleOne);
    checkBookTitle(libraryNameTwo, testTitleTwo);
  }

  private void checkBookTitle(String libraryName, String testTitle) {
    var booksResponse = eBookTestClient.getBookList(libraryName, this.port);
    assertEquals(HttpStatus.OK, booksResponse.getStatusCode());

    var books = booksResponse.getBody();
    assertNotNull(books);
    assertEquals(1, books.size());
    assertEquals(testTitle, books.get(0).getTitle());
  }

  private void modifyLibraryOnDisk(String libraryName, String oldTitle, String newTitle) throws IOException {
    var libraryJson = FileUtils.readFileToString(Paths.get(ESDATA_TEST_PATH, libraryName, "library.json").toFile(), Charset.defaultCharset());
    var library = new Gson().fromJson(libraryJson, Library.class);
    assertTrue(library.getBooks().size() >= 1);
    assertEquals(oldTitle, library.getBooks().get(0).getTitle());
    library.getBooks().get(0).setTitle(newTitle);
    FileUtils.writeStringToFile(Paths.get(ESDATA_TEST_PATH, libraryName, "library.json").toFile(), new Gson().toJson(library), Charset.defaultCharset());
  }
}
