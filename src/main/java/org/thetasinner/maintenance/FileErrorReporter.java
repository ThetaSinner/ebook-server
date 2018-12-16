package org.thetasinner.maintenance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thetasinner.web.model.ReportModel;

import java.io.File;

@Component
public class FileErrorReporter implements IErrorReporter {
  private String dataPath;

  @Value("${es.data.path:esdata}")
  private void setDataPath(String param) {
    if (param.charAt(param.length() - 1) != File.separatorChar) {
      param += File.separator;
    }
    dataPath = param;
  }

  @Override
  public void findUnreferencedBooks(ReportModel report) {
    
  }
}
