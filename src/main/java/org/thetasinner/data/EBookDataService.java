package org.thetasinner.data;

import com.github.fge.jsonpatch.JsonPatchException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.content.BookService;
import org.thetasinner.data.content.LibraryService;
import org.thetasinner.data.content.VideoService;
import org.thetasinner.data.exception.EBookDataServiceException;
import org.thetasinner.data.exception.EBookDataServiceInputValidationException;
import org.thetasinner.data.exception.EBookFileNotFoundException;
import org.thetasinner.data.exception.InvalidRequestException;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.model.Video;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.StorageException;
import org.thetasinner.web.events.ChangeEventData;
import org.thetasinner.web.events.LibraryChangeService;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.CommitLibrary;
import org.thetasinner.web.model.CommitRequest;

import javax.servlet.ServletOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.thetasinner.web.events.ChangeEventData.ChangeType.BookCreated;
import static org.thetasinner.web.events.ChangeEventData.ChangeType.BookUpdated;
import static org.thetasinner.web.events.ChangeEventData.ChangeType.VideoCreated;
import static org.thetasinner.web.events.ChangeEventData.ChangeType.VideoUpdated;

@Service
public class EBookDataService {
  private static final Logger LOG = LoggerFactory.getLogger(EBookDataService.class);

  private final LibraryChangeService libraryChangeService;

  private final LibraryService libraryService;

  private final BookService bookService;

  private final ILibraryStorage libraryStorage;

  private final ContentTypeService contentTypeService;

  private final VideoService videoService;

  @Autowired
  public EBookDataService(LibraryChangeService changeService, LibraryService libraryService, BookService bookService, ILibraryStorage libraryStorage, ContentTypeService contentTypeService, VideoService videoService) {
    this.libraryChangeService = changeService;
    this.libraryService = libraryService;
    this.bookService = bookService;
    this.libraryStorage = libraryStorage;
    this.contentTypeService = contentTypeService;
    this.videoService = videoService;
  }

  public void commit(CommitRequest commitRequest) {
    if (commitRequest.getCommitAll() != null || commitRequest.getCommitAndUnloadAll() != null) {
      commitAllLibraries(commitRequest);
    } else {
      commitSpecifiedLibraries(commitRequest);
    }
  }

  private void commitSpecifiedLibraries(CommitRequest commitRequest) {
    List<CommitLibrary> commitLibraries = commitRequest.getCommitLibraries();
    if (commitLibraries == null) {
      throw new EBookDataServiceException("Cannot update no libraries");
    }

    commitLibraries.forEach(request -> libraryService.save(request.getLibraryName(), request.getUnload()));
  }

  private void commitAllLibraries(CommitRequest commitRequest) {
    var commitAll = Boolean.TRUE.equals(commitRequest.getCommitAll());
    var commitAndUnloadAll = Boolean.TRUE.equals(commitRequest.getCommitAndUnloadAll());
    if (commitAll || commitAndUnloadAll) {
      libraryService.getLibraries().forEach(libraryName -> libraryService.save(libraryName, commitAndUnloadAll));
    }
  }

  public void createLibrary(String name) {
    if (StringUtils.isEmpty(name)) {
      throw new EBookDataServiceInputValidationException("Library name must not be empty");
    }

    libraryService.create(name);
  }

  public List<Integer> storeAll(String name, MultipartFile[] files) {
    if (files.length == 0) {
      throw new EBookDataServiceInputValidationException("No files provided to upload");
    }

    List<Integer> failed = new ArrayList<>();

    var storeIndex = 0;
    for (MultipartFile file : files) {
      try {
        store(name, file);
        storeIndex++;
      } catch (StorageException | IOException e) {
        LOG.error("Storage error", e);
        failed.add(storeIndex);
      }
    }

    return failed;
  }

  public void recoverStoredBook(String name, String id) {
    var book = bookService.recoverStoredBook(name, id);

    var eventData = new ChangeEventData(BookCreated, book.getId());
    libraryChangeService.publish(name, eventData);
  }

  private void store(String libraryName, MultipartFile file) throws StorageException, IOException {
    if (file.isEmpty()) {
      throw new StorageException("File is empty");
    }

    String contentType = file.getContentType();
    if (contentType == null) {
      LOG.error("Will not store file [{}] because it does not have a content type.", file.getName());
    }
    else if (contentTypeService.isBookContentType(contentType)) {
      var book = bookService.storeBook(libraryName, file);

      var eventData = new ChangeEventData(BookCreated, book.getId());
      libraryChangeService.publish(libraryName, eventData);
    }
    else if (contentTypeService.isVideoContentType(contentType)) {
      var video = videoService.storeVideo(libraryName, file);

      var eventData = new ChangeEventData(VideoCreated, video.getId());
      libraryChangeService.publish(libraryName, eventData);
    }
  }

  public List<Book> getBooks(String name) {
    List<Book> books = bookService.getBooks(name);

    return books == null ? new ArrayList<>() : books;
  }

  public Book createBook(String name, BookAddRequest bookAddRequest) {
    if (StringUtils.isBlank(name)) {
      throw new InvalidRequestException("Invalid library name");
    }

    if (bookAddRequest == null || bookAddRequest.getUrl() == null || bookAddRequest.getType() == null) {
      throw new InvalidRequestException("Invalid add book request");
    }

    Book book;
    switch (bookAddRequest.getType()) {
      case LocalUnmanaged:
        Path path = Paths.get(bookAddRequest.getUrl());
        if (!Files.exists(path)) {
          throw new EBookFileNotFoundException("Won't add book because the file does not exist");
        }

        book = bookService.createBook(name, bookAddRequest.getUrl(), TypedUrl.Type.LocalUnmanaged);
        break;
      case WebLink:
        book = bookService.createBook(name, bookAddRequest.getUrl(), TypedUrl.Type.WebLink);
        break;
      case Other:
        book = bookService.createBook(name, bookAddRequest.getUrl(), TypedUrl.Type.Other);
        break;
      default:
        throw new InvalidRequestException(String.format("Cannot create a book of type [%s]", bookAddRequest.getType()));
    }

    // Publish book created change event.
    var eventData = new ChangeEventData(BookCreated, book.getId());
    libraryChangeService.publish(name, eventData);

    return book;
  }

  @EmitDeleteEvent
  public void deleteBook(String id, String name) {
    if (StringUtils.isBlank(id)) {
      throw new InvalidRequestException("Missing request param: id");
    }

    bookService.deleteBook(name, id);
  }

  public Book updateBook(String id, String name, String bookPatch) throws IOException, JsonPatchException {
    if (StringUtils.isEmpty(id)) {
      throw new InvalidRequestException("Missing request param: id");
    }

    if (bookPatch == null) {
      throw new InvalidRequestException("Missing request body");
    }

    Book book = bookService.updateBook(id, name, bookPatch);

    // Publish book updated change event.
    ChangeEventData eventData = new ChangeEventData(BookUpdated, id);
    libraryChangeService.publish(name, eventData);

    return book;
  }

  public List<String> getLibraries() {
    return libraryService.getLibraries();
  }

  public String getBook(String id, String name, OutputStream outputStream) throws IOException {
    var book = bookService.getBook(name, id);
    FileInputStream inputStream = libraryStorage.getBookInputStream(book);
    IOUtils.copy(inputStream, outputStream);
    return "application/pdf";
  }

  public List<Integer> uploadCover(String id, String name, MultipartFile cover) {
    if (cover == null || cover.isEmpty()) {
      return Collections.singletonList(0);
    }

    List<Integer> failedUploadIndices = new ArrayList<>(1);
    try {
      var book = bookService.getBook(name, id);
      libraryStorage.storeCover(book, cover);
    } catch (StorageException e) {
      failedUploadIndices.add(0);
    }

    return failedUploadIndices;
  }

  public String getCover(String bookId, String libraryName, ServletOutputStream outputStream) throws IOException {
    var book = bookService.getBook(libraryName, bookId);
    FileInputStream inputStream = libraryStorage.getCoverInputStream(book);
    IOUtils.copy(inputStream, outputStream);
    return "image/png";
  }

  public void deleteBookFromStorage(String libraryName, String bookId) throws IOException {
    bookService.deleteBookFromStorage(libraryName, bookId);
  }

  public String getVideo(String id, String name, ServletOutputStream outputStream) throws IOException {
    var video = videoService.getVideo(name, id);
    var videoContent = libraryStorage.getVideoInputStream(video);
    IOUtils.copy(videoContent.getInputStream(), outputStream);

    return videoContent.getContentType();
  }

  public List<Video> getVideos(String name) {
    var videos = videoService.getVideos(name);

    return videos == null ? new ArrayList<>() : videos;
  }

  public Video updateVideo(String id, String libraryName, String videoPatch) throws IOException, JsonPatchException {
    if (StringUtils.isEmpty(id)) {
      throw new InvalidRequestException("Missing request param: id");
    }

    if (videoPatch == null) {
      throw new InvalidRequestException("Missing request body");
    }

    Video video = videoService.updateVideo(id, libraryName, videoPatch);

    // Publish book updated change event.
    ChangeEventData eventData = new ChangeEventData(VideoUpdated, id);
    libraryChangeService.publish(libraryName, eventData);

    return video;
  }
}
