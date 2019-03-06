package org.thetasinner.maintenance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thetasinner.data.EBookDataService;
import org.thetasinner.web.model.ReportFixFieldModel;
import org.thetasinner.web.model.ReportFixModel;
import org.thetasinner.web.model.ReportModel;
import org.thetasinner.web.model.UnlistedBook;

import java.io.FileNotFoundException;

import static org.thetasinner.web.model.ReportFixFieldModel.*;
import static org.thetasinner.web.model.ReportFixFieldModel.FixAction.*;

@Service
public class MaintenanceService {
  private final IErrorReporter errorReporter;
  private final LibraryMetricsReporter libraryMetricsReporter;
  private final EBookDataService eBookDataService;

  @Autowired
  public MaintenanceService(IErrorReporter errorReporter, LibraryMetricsReporter libraryMetricsReporter, EBookDataService eBookDataService) {
    this.errorReporter = errorReporter;
    this.libraryMetricsReporter = libraryMetricsReporter;
    this.eBookDataService = eBookDataService;
  }

  public ReportModel createReport(String libraryName) throws FileNotFoundException {
    var report = new ReportModel();

    errorReporter.findUnreferencedBooks(libraryName, report);
    errorReporter.findUnreachableBooks(libraryName, report);

    libraryMetricsReporter.reportMetrics(libraryName, report);
    libraryMetricsReporter.reportCurationMetrics(libraryName, report);

    return report;
  }

  public ReportModel applyReportFixes(String libraryName, String reportId, ReportFixModel reportFixModel) throws FileNotFoundException {
    if (!reportId.equals(reportFixModel.getReportModel().getReportId())) {
      throw new IllegalStateException("Mismatched report provided");
    }

    reportFixModel.reportFixFieldModels.forEach(fixField -> {
      var searchFieldId = fixField.getReportFixFieldId();

      reportFixModel.getReportModel().getUnlistedBooks().forEach(unlistedBook -> {
        if (searchFieldId.equals(unlistedBook.getIssueId())) {
          fixUnlistedBook(libraryName, unlistedBook, fixField.fixAction);
        }
      });
    });

    return createReport(libraryName);
  }

  private void fixUnlistedBook(String libraryName, UnlistedBook unlistedBook, FixAction fixAction) {
    switch (fixAction) {
      case AddUnlisted:

        break;
      case RemoveUnlisted:
        this.eBookDataService.deleteBookFromStorage(libraryName, unlistedBook.getMissingBookId());
        break;
      default:
        throw new IllegalStateException(String.format("Cannot apply action [%s] for fixing an unlisted book.", fixAction));
    }
  }
}
