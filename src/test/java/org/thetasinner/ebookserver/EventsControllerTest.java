package org.thetasinner.ebookserver;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import org.thetasinner.ebookserver.helper.EBookTestClient;
import org.thetasinner.ebookserver.helper.TestDataHelper;
import org.thetasinner.ebookserver.helper.TestInfrastructureHelper;
import org.thetasinner.ebookserver.helper.UrlHelper;
import org.thetasinner.web.events.ChangeEventData;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.BookUpdateRequest;
import org.thetasinner.web.model.RequestBase;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-event.properties")
public class EventsControllerTest {
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
    public void eventTriggeredWhenBookAdded() throws InterruptedException {
        var NUMBER_OF_COUNT_DOWNS_REQUIRED = 1;
        var lock = new CountDownLatch(NUMBER_OF_COUNT_DOWNS_REQUIRED);

        String libraryName = testDataHelper.getCurrentMethodName();
        eBookTestClient.createLibrary(libraryName, port);

        var eventStream = createSubscriptionRequest(libraryName);

        var changes = new ArrayList<ChangeEventData>();
        captureEvents(lock, eventStream, changes);

        sendAddRequest(libraryName);

        var countDownSucceeded = lock.await(5, TimeUnit.SECONDS);
        assertTrue(countDownSucceeded);

        assertEquals(1, changes.size());
        assertEquals(ChangeEventData.ChangeType.BookCreated, changes.get(0).getChangeType());
    }

    @Test
    public void eventTriggeredForEachBookAdded() throws InterruptedException {
        var NUMBER_OF_COUNT_DOWNS_REQUIRED = 3;
        var lock = new CountDownLatch(NUMBER_OF_COUNT_DOWNS_REQUIRED);

        String libraryName = testDataHelper.getCurrentMethodName();
        eBookTestClient.createLibrary(libraryName, port);

        var eventStream = createSubscriptionRequest(libraryName);

        var changes = new ArrayList<ChangeEventData>();
        captureEvents(lock, eventStream, changes);

        sendAddRequest(libraryName);
        sendAddRequest(libraryName);
        sendAddRequest(libraryName);

        var countDownSucceeded = lock.await(5, TimeUnit.SECONDS);
        assertTrue(countDownSucceeded);

        assertEquals(3, changes.size());
        changes.forEach(change -> {
            assertEquals(ChangeEventData.ChangeType.BookCreated, change.getChangeType());
        });
    }

    @Test
    public void eventTriggeredWhenBookUpdated() throws InterruptedException {
        var NUMBER_OF_COUNT_DOWNS_REQUIRED = 1;
        var lock = new CountDownLatch(NUMBER_OF_COUNT_DOWNS_REQUIRED);

        String libraryName = testDataHelper.getCurrentMethodName();
        eBookTestClient.createLibrary(libraryName, port);

        sendAddRequest(libraryName);

        var bookList = eBookTestClient.getBookList(libraryName, port);
        var bookId = bookList.getBody().get(0).getId();

        var eventStream = createSubscriptionRequest(libraryName);

        var changes = new ArrayList<ChangeEventData>();
        captureEvents(lock, eventStream, changes);

        var request = new RequestBase<BookUpdateRequest>();
        request.setName(libraryName);
        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        request.setRequest(bookUpdateRequest);
        bookUpdateRequest.setTitle("I updated the title!");
        eBookTestClient.updateBook(request, bookId, port);

        var countDownSucceeded = lock.await(5, TimeUnit.SECONDS);
        assertTrue(countDownSucceeded);

        assertEquals(1, changes.size());
        assertEquals(ChangeEventData.ChangeType.BookUpdated, changes.get(0).getChangeType());
    }

    @Test
    public void eventTriggeredWhenBookDeleted() throws InterruptedException {
        var NUMBER_OF_COUNT_DOWNS_REQUIRED = 1;
        var lock = new CountDownLatch(NUMBER_OF_COUNT_DOWNS_REQUIRED);

        String libraryName = testDataHelper.getCurrentMethodName();
        eBookTestClient.createLibrary(libraryName, port);

        sendAddRequest(libraryName);

        var bookList = eBookTestClient.getBookList(libraryName, port);
        var bookId = bookList.getBody().get(0).getId();

        var eventStream = createSubscriptionRequest(libraryName);

        var changes = new ArrayList<ChangeEventData>();
        captureEvents(lock, eventStream, changes);

        eBookTestClient.deleteBook(libraryName, bookId, port);

        var countDownSucceeded = lock.await(5, TimeUnit.SECONDS);
        assertTrue(countDownSucceeded);

        assertEquals(1, changes.size());
        assertEquals(ChangeEventData.ChangeType.BookDeleted, changes.get(0).getChangeType());
    }

    @Test
    public void multipleSubscribersCanReceiveSameNotification() throws InterruptedException {
        var NUMBER_OF_COUNT_DOWNS_REQUIRED = 2;
        var lock = new CountDownLatch(NUMBER_OF_COUNT_DOWNS_REQUIRED);

        String libraryName = testDataHelper.getCurrentMethodName();
        eBookTestClient.createLibrary(libraryName, port);

        var eventStreamOne = createSubscriptionRequest(libraryName);
        var eventStreamTwo = createSubscriptionRequest(libraryName);

        var changes = new CopyOnWriteArrayList<ChangeEventData>();
        captureEvents(lock, eventStreamOne, changes);
        captureEvents(lock, eventStreamTwo, changes);

        sendAddRequest(libraryName);

        var countDownSucceeded = lock.await(5, TimeUnit.SECONDS);
        assertTrue(countDownSucceeded);

        // Expect two add requests which are for the same book.
        assertEquals(2, changes.size());
        changes.forEach(change -> assertEquals(ChangeEventData.ChangeType.BookCreated, change.getChangeType()));
        assertEquals(changes.get(0).getBookId(), changes.get(1).getBookId());
    }

    private void sendAddRequest(String libraryName) {
        var request = new RequestBase<BookAddRequest>();
        request.setName(libraryName);
        BookAddRequest bookAddRequest = new BookAddRequest();
        request.setRequest(bookAddRequest);
        bookAddRequest.setType(BookAddRequest.Type.WebLink);
        bookAddRequest.setUrl("http://hello.world/add");

        eBookTestClient.createBook(request, this.port);
    }

    private void captureEvents(CountDownLatch lock, Flux<ServerSentEvent<ChangeEventData>> eventStream, List<ChangeEventData> changes) throws InterruptedException {
        eventStream.subscribe(
                content -> {
                    changes.add(content.data());
                    lock.countDown();
                },
                error -> {
                    error.printStackTrace();
                    fail();
                },
                () -> System.out.println("Subscription completed."));

        // Things that run after the subscribe should be chained into the callbacks. But more code needs to run in order
        // to trigger an event before any callback will run. This sleep gives the subscription request time to run
        Thread.sleep(300);
    }

    private Flux<ServerSentEvent<ChangeEventData>> createSubscriptionRequest(String libraryName) {
        var client = WebClient.create(urlHelper.buildRequestUrl("/events", port));
        ParameterizedTypeReference<ServerSentEvent<ChangeEventData>> type
                = new ParameterizedTypeReference<>() {
        };

        return client.get()
                .uri("/subscribe/{libraryName}", libraryName)
                .retrieve()
                .bodyToFlux(type);
    }
}
