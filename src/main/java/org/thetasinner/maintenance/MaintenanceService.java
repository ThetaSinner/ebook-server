package org.thetasinner.maintenance;

import org.springframework.stereotype.Service;
import org.thetasinner.web.model.ReportModel;

import java.io.FileNotFoundException;

@Service
public class MaintenanceService {
  private final IErrorReporter errorReporter;

  public MaintenanceService(IErrorReporter errorReporter) {
    this.errorReporter = errorReporter;
  }

  public ReportModel createReport(String libraryName) throws FileNotFoundException {
    var report = new ReportModel();

    errorReporter.findUnreferencedBooks(libraryName, report);

    return report;
  }
}
