package org.thetasinner.maintenance;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.file.FileLibraryStorage;
import org.thetasinner.web.model.MissingBook;
import org.thetasinner.web.model.ReportModel;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

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
    if (param.charAt(param.length() - 1) != File.separatorChar) {
      param += File.separator;
    }
    dataPath = param;
  }

  @Override
  public void findUnreferencedBooks(String libraryName, ReportModel report) {
    var listedBooks = libraryStorage.load(libraryName)
            .getBooks()
            .stream()
            .filter(book -> book.getUrl().getType() == TypedUrl.Type.LocalManaged)
            .map(book -> Paths.get(book.getUrl().getValue()).getParent().getFileName().toString())
            .collect(toSet());

    var storedBooks = Arrays.stream(
            Objects.requireNonNull(
                    Paths.get(dataPath, libraryName)
                    .toFile()
                    .listFiles(File::isDirectory)))
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
}
