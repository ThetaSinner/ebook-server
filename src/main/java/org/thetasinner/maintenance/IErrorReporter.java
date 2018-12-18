package org.thetasinner.maintenance;

import org.thetasinner.web.model.ReportModel;

import java.io.FileNotFoundException;

public interface IErrorReporter {
  void findUnreferencedBooks(String libraryName, ReportModel report) throws FileNotFoundException;
}
