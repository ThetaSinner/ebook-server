package org.thetasinner.maintenance;

import org.thetasinner.web.model.ReportModel;

public interface IErrorReporter {
  void findUnreferencedBooks(ReportModel report);
}
