package org.thetasinner.ebookserver;

import ch.qos.logback.core.util.FileUtil;
import net.bytebuddy.pool.TypePool;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.jni.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.thetasinner.web.model.EmptyJsonResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EBookControllerTest {
    @LocalServerPort
    private int port;

    @Value("${es.data.path}")
    private String eBookDataPath;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() throws IOException {
        cleanup();
    }

    @After
    public void teardown() throws IOException {
        cleanup();
    }

    @Test
    public void createNewLibrary() throws Exception {
        var uriParams = new HashMap<String, String>();
        uriParams.put("name", "test-name");

        var response = restTemplate.postForEntity("http://localhost:" + port + "/libraries?name={name}", new EmptyRequest(), EmptyJsonResponse.class, uriParams);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var path = Paths.get(eBookDataPath, "test-name");
        assertTrue(Files.exists(path));
    }

    private void cleanup() throws IOException {
        var testLibraryPath = Paths.get(eBookDataPath);
        if (Files.exists(testLibraryPath)){
            FileUtils.deleteDirectory(testLibraryPath.toFile());
        }
    }
}
