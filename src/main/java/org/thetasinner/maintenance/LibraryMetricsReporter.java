package org.thetasinner.maintenance;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.file.FileLibraryStorage;
import org.thetasinner.web.model.BookWithMissingData;
import org.thetasinner.web.model.ReportCurationMetricsModel;
import org.thetasinner.web.model.ReportMetricsModel;
import org.thetasinner.web.model.ReportModel;

import java.util.UUID;

@Component
public class LibraryMetricsReporter {
    private static final Logger LOG = LoggerFactory.getLogger(FileLibraryStorage.class);

    private final ILibraryStorage libraryStorage;

    @Autowired
    public LibraryMetricsReporter(ILibraryStorage libraryStorage) {
        this.libraryStorage = libraryStorage;
    }

    void reportMetrics(String libraryName, ReportModel report) {
        LOG.trace("Reporting count metrics for library [{}]", libraryName);

        var library = libraryStorage.load(libraryName);

        ReportMetricsModel metrics = new ReportMetricsModel();
        report.setMetrics(metrics);

        metrics.setNumberOfBooks(library.getBooks().size());

        metrics.setNumberOfWebLinkBooks((int) library.getBooks().stream().filter(book -> book.getUrl().getType() == TypedUrl.Type.WebLink).count());
        metrics.setNumberOfLocalManagedBooks((int) library.getBooks().stream().filter(book -> book.getUrl().getType() == TypedUrl.Type.LocalManaged).count());
        metrics.setNumberOfLocalUnmanagedBooks((int) library.getBooks().stream().filter(book -> book.getUrl().getType() == TypedUrl.Type.LocalUnmanaged).count());
    }

    void reportCurationMetrics(String libraryName, ReportModel report) {
        LOG.trace("Reporting curation metrics for library [{}]", libraryName);

        var library = libraryStorage.load(libraryName);

        ReportCurationMetricsModel curationMetrics = new ReportCurationMetricsModel();
        report.setCurationMetrics(curationMetrics);

        library.getBooks().forEach(book -> {
            if (StringUtils.isBlank(book.getTitle())) {
                curationMetrics.getBooksWithMissingTitles().add(
                        createBookWithMissingData(book)
                );
            }

            if (StringUtils.isBlank(book.getPublisher())) {
                curationMetrics.getBooksWithMissingPublisher().add(
                        createBookWithMissingData(book)
                );
            }

            if (book.getAuthors().isEmpty()) {
                curationMetrics.getBooksWithMissingAuthors().add(
                        createBookWithMissingData(book)
                );
            }
        });
    }

    private BookWithMissingData createBookWithMissingData(Book book) {
        var bookWithMissingData = new BookWithMissingData();

        bookWithMissingData.setBookId(book.getId());
        bookWithMissingData.setReportId(UUID.randomUUID().toString());

        return bookWithMissingData;
    }
}
