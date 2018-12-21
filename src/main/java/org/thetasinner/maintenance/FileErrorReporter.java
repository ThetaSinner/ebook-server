package org.thetasinner.maintenance;

import com.google.common.collect.Sets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.file.FileLibraryStorage;
import org.thetasinner.web.model.MissingBook;
import org.thetasinner.web.model.ReportModel;
import org.thetasinner.web.model.UnreachableBooksModel;
import org.thetasinner.web.model.UnreachableWebLink;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;
import static org.thetasinner.data.model.TypedUrl.*;
import static org.thetasinner.data.model.TypedUrl.Type.*;

@Component
public class FileErrorReporter implements IErrorReporter {
  private static final Logger LOG = LoggerFactory.getLogger(FileLibraryStorage.class);

  private final ILibraryStorage libraryStorage;

  private String dataPath;

  @Autowired
  public FileErrorReporter(ILibraryStorage libraryStorage) {
    this.libraryStorage = libraryStorage;
  }

  @Value("${es.data.path:esdata}")
  private void setDataPath(String param) {
    LOG.trace("esdata path is set to [{}]", param);

    if (param.charAt(param.length() - 1) != File.separatorChar) {
      param += File.separator;
    }
    dataPath = param;
  }

  @Override
  public void findUnreferencedBooks(String libraryName, ReportModel report) throws FileNotFoundException {
    LOG.trace("Finding unreferenced books for library [{}]", libraryName);

    var listedBooks = libraryStorage.load(libraryName)
            .getBooks()
            .stream()
            .filter(book -> book.getUrl().getType() == LocalManaged)
            .map(book -> Paths.get(book.getUrl().getValue()).getParent().getFileName().toString())
            .collect(toSet());

    var libraryItemContainerFolders = Paths.get(dataPath, libraryName)
            .toFile()
            .listFiles(File::isDirectory);

    if (libraryItemContainerFolders == null) {
      throw new FileNotFoundException("No library containers");
    }

    var storedBooks = Arrays.stream(libraryItemContainerFolders)
            .map(File::getName)
            .collect(toSet());

    var booksWhichAreNotStored = Sets.difference(listedBooks, storedBooks);
    var booksWhichAreNotListed = Sets.difference(storedBooks, listedBooks);

    booksWhichAreNotStored.forEach(
            notStoredBook -> report.getMissingBooks().add(new MissingBook(notStoredBook, UUID.randomUUID().toString()))
    );

    booksWhichAreNotListed.forEach(
            notListedBook -> report.getUnlistedBooks().add(new MissingBook(notListedBook, UUID.randomUUID().toString()))
    );
  }

  @Override
  public void findUnreachableBooks(String libraryName, ReportModel report) {
    LOG.trace("Finding unreachable books in library [{}]", libraryName);

    report.setUnreachableBooksModel(new UnreachableBooksModel());

    var library = libraryStorage.load(libraryName);
    library.getBooks().forEach(book -> {
      switch (book.getUrl().getType()) {
        case WebLink:
          testWebLinkReachable(book, report.getUnreachableBooksModel());
          break;
        default:
          LOG.warn("Not trying to reach book [{}] because its URL type [{}] excludes it from this reporting check", book.getId(), book.getUrl().getType());
      }
    });
  }

  private void testWebLinkReachable(Book book, UnreachableBooksModel report) {
    var client = HttpClientBuilder.create().build();
    var request = new HttpGet(book.getUrl().getValue());

    try {
      var response = client.execute(request);

      if (response.getStatusLine().getStatusCode() != 200) {
        addUnreachableWebLink(book, report, response.getStatusLine().getStatusCode());
      }
    } catch (IOException e) {
      LOG.error(String.format("There was an error while testing web link [%s]", book.getUrl().getValue()), e);

      addUnreachableWebLink(book, report, -1);
    }
  }

  private void addUnreachableWebLink(Book book, UnreachableBooksModel report, int statusCode) {
    UnreachableWebLink unreachableWebLink = new UnreachableWebLink();
    report.getWebLinks().add(unreachableWebLink);

    unreachableWebLink.setBookId(book.getId());
    unreachableWebLink.setReportId(UUID.randomUUID().toString());
    unreachableWebLink.setStatusCode(statusCode);
  }
}
