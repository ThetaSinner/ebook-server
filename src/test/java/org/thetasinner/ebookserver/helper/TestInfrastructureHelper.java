package org.thetasinner.ebookserver.helper;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestInfrastructureHelper {
    public static final String ESDATA_TEST_PATH = "esdata-test";

    public static void cleanup() throws IOException {
        var testLibraryPath = Paths.get(ESDATA_TEST_PATH);
        if (Files.exists(testLibraryPath)){
            FileUtils.deleteDirectory(testLibraryPath.toFile());
        }
    }
}
