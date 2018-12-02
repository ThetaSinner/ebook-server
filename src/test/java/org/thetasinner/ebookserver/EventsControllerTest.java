package org.thetasinner.ebookserver;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import org.thetasinner.ebookserver.helper.EBookTestClient;
import org.thetasinner.ebookserver.helper.TestDataHelper;
import org.thetasinner.ebookserver.helper.TestInfrastructureHelper;
import org.thetasinner.ebookserver.helper.UrlHelper;
import org.thetasinner.web.events.ChangeEventData;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.RequestBase;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        TestInfrastructureHelper.cleanup();
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

        lock.await(5, TimeUnit.SECONDS);

        assertEquals(1, changes.size());
    }

    @Test
    public void eventTriggeredForEachBookAdded() throws InterruptedException {
        var NUMBER_OF_COUNT_DOWNS_REQUIRED = 1;
        var lock = new CountDownLatch(NUMBER_OF_COUNT_DOWNS_REQUIRED);

        String libraryName = testDataHelper.getCurrentMethodName();
        eBookTestClient.createLibrary(libraryName, port);

        var eventStream = createSubscriptionRequest(libraryName);

        var changes = new ArrayList<ChangeEventData>();
        captureEvents(lock, eventStream, changes);

        sendAddRequest(libraryName);
        sendAddRequest(libraryName);
        sendAddRequest(libraryName);

        lock.await(5, TimeUnit.SECONDS);

        assertEquals(3, changes.size());
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

    private void captureEvents(CountDownLatch lock, Flux<ServerSentEvent<ChangeEventData>> eventStream, ArrayList<ChangeEventData> changes) throws InterruptedException {
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
        Thread.sleep(100);
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
