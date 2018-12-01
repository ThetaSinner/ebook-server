package org.thetasinner.ebookserver;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import org.thetasinner.ebookserver.helper.EBookTestClient;
import org.thetasinner.ebookserver.helper.TestDataHelper;
import org.thetasinner.ebookserver.helper.TestInfrastructureHelper;
import org.thetasinner.ebookserver.helper.UrlHelper;
import reactor.core.publisher.Flux;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    public void createSubscription() {
        String libraryName = testDataHelper.getCurrentMethodName();
        eBookTestClient.createLibrary(libraryName, port);

        var client = WebClient.create(urlHelper.buildRequestUrl("/events", port));
        ParameterizedTypeReference<ServerSentEvent<String>> type
                = new ParameterizedTypeReference<>() {
        };

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri("/subscribe/{libraryName}", libraryName)
                .retrieve()
                .bodyToFlux(type);

        eventStream.subscribe(
                System.out::println,
                error -> {
                    error.printStackTrace();
                    fail();
                },
                () -> System.out.println("Completed!!!"));
    }
}
