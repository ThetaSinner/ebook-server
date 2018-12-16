package org.thetasinner.maintenance;

import org.springframework.stereotype.Service;
import org.thetasinner.web.model.ReportModel;

@Service
public class MaintenanceService {
  private final IErrorReporter errorReporter;

  public MaintenanceService(IErrorReporter errorReporter) {
    this.errorReporter = errorReporter;
  }

  public ReportModel createReport(String libraryName) {
    var report = new ReportModel();

    errorReporter.findUnreferencedBooks(report);

    return report;
  }
}
