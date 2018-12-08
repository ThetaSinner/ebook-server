package org.thetasinner.ebookserver.helper;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestInfrastructureHelper {

  public static void cleanup(String path) throws IOException {
    var testLibraryPath = Paths.get(path);
    if (Files.exists(testLibraryPath)) {
      FileUtils.deleteDirectory(testLibraryPath.toFile());
    }
  }
}
