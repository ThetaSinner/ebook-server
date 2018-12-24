package org.thetasinner.ebookserver;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.thetasinner.ebookserver.helper.EBookTestClient;
import org.thetasinner.ebookserver.helper.TestDataHelper;
import org.thetasinner.ebookserver.helper.TestInfrastructureHelper;
import org.thetasinner.ebookserver.helper.UrlHelper;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.ReportModel;
import org.thetasinner.web.model.RequestBase;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-maintenance.properties")
public class MaintenanceControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private EBookTestClient eBookTestClient;

    @Autowired
    private UrlHelper urlHelper;

    // Should be BeforeAll, but running with SpringRunner which is using JUnit4.
    @BeforeClass
    public static void setup() throws IOException {
        TestInfrastructureHelper.cleanup("esdata-test-event");
    }

    @Test
    public void booksInLibraryAreCounted() {
        var libraryName = testDataHelper.getCurrentMethodName();
        eBookTestClient.createLibrary(libraryName, port);

        addWebLink(libraryName);
        uploadBook(libraryName);

        var report = getReportModel(libraryName);
        assertNotNull(report.getMetrics());
        assertEquals(2, report.getMetrics().getNumberOfBooks());
    }

    @Test
    public void breakdownOfBookCountsByTypeIsGiven() {
        var libraryName = testDataHelper.getCurrentMethodName();
        eBookTestClient.createLibrary(libraryName, port);

        addWebLink(libraryName);
        uploadBook(libraryName);

        var report = getReportModel(libraryName);
        assertNotNull(report.getMetrics());
        assertEquals(1, report.getMetrics().getNumberOfLocalManagedBooks());
        assertEquals(0, report.getMetrics().getNumberOfLocalUnmanagedBooks());
        assertEquals(1, report.getMetrics().getNumberOfWebLinkBooks());
    }

    private ReportModel getReportModel(String libraryName) {
        var response = eBookTestClient.getReport(libraryName, port);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var report = response.getBody();
        assertNotNull(report);
        return report;
    }

    private void uploadBook(String libraryName) {
        eBookTestClient.uploadBook(libraryName, "test-ebook/document.pdf", port);
    }

    private void addWebLink(String libraryName) {
        var bookAddRequest = new RequestBase<BookAddRequest>();
        bookAddRequest.setName(libraryName);
        BookAddRequest addRequest = new BookAddRequest();
        bookAddRequest.setRequest(addRequest);
        addRequest.setType(BookAddRequest.Type.WebLink);
        addRequest.setUrl("https://github.com/ThetaSinner/ebook-server/blob/master/src/test/resources/test-ebook/document.pdf");
        eBookTestClient.createBook(bookAddRequest, port);
    }
}
