package org.thetasinner.maintenance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thetasinner.web.model.ReportModel;

import java.io.FileNotFoundException;

@Service
public class MaintenanceService {
  private final IErrorReporter errorReporter;
  private final LibraryMetricsReporter libraryMetricsReporter;

  @Autowired
  public MaintenanceService(IErrorReporter errorReporter, LibraryMetricsReporter libraryMetricsReporter) {
    this.errorReporter = errorReporter;
    this.libraryMetricsReporter = libraryMetricsReporter;
  }

  public ReportModel createReport(String libraryName) throws FileNotFoundException {
    var report = new ReportModel();

    errorReporter.findUnreferencedBooks(libraryName, report);

    libraryMetricsReporter.reportCounts(libraryName, report);

    return report;
  }
}
