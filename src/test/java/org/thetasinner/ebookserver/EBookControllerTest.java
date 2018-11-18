package org.thetasinner.ebookserver;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public void createNewLibrary() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var path = Paths.get(eBookDataPath, libraryName);
        assertTrue(Files.exists(path));
    }

    @Test
    public void existingLibraryCannotBeOverwritten() {
        var libraryName = getCurrentMethodName();
        var response = createProject(libraryName);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = createProject(libraryName);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void libraryNameMustNotBeEmpty() {
        var response = createProject("");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("E-Book data service input failed validation [Library name must not be empty]", response.getBody());
    }

    private ResponseEntity<String> createProject(String libraryName) {
        var uriParams = new HashMap<String, String>();
        uriParams.put("name", libraryName);

        return restTemplate.postForEntity("http://localhost:" + port + "/libraries?name={name}", new EmptyRequest(), String.class, uriParams);
    }

    private String getCurrentMethodName() {
        // Indexing to 2 removes 'getStackTrace' and the current method name 'getCurrentMethodName'
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    private void cleanup() throws IOException {
        var testLibraryPath = Paths.get(eBookDataPath);
        if (Files.exists(testLibraryPath)){
            FileUtils.deleteDirectory(testLibraryPath.toFile());
        }
    }
}
