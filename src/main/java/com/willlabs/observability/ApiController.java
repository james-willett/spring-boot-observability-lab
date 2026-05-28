package com.willlabs.observability;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

/**
 * Four REST endpoints used by the Pluralsight observability lab.
 * Each one is shaped to produce a different, visible signal in
 * Actuator metrics and Zipkin traces once the learner enables them.
 *
 *   GET /hello   — single-span happy path
 *   GET /slow    — long latency span (default 500 ms, overridable)
 *   GET /chain   — internal HTTP call producing a multi-span trace
 *   GET /flaky   — random 500 to drive a non-zero error rate
 *                  (NOT /error — Spring Boot's BasicErrorController owns /error;
 *                   shadowing it breaks default error handling for the whole app.)
 */
@RestController
public class ApiController {

    private final RestClient restClient;

    /**
     * The auto-configured RestClient.Builder is what makes /chain produce
     * a real multi-span trace: Spring's instrumentation injects the
     * tracing interceptor, which propagates the B3 / traceparent headers
     * on the outbound call. A hand-rolled HttpClient would NOT propagate
     * trace context and the child span would appear as a disconnected
     * second trace in Zipkin.
     */
    public ApiController(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://localhost:8080").build();
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, observability!";
    }

    @GetMapping("/slow")
    public String slow(@RequestParam(defaultValue = "500") long ms) throws InterruptedException {
        Thread.sleep(ms);
        return "Slow response after " + ms + "ms";
    }

    @GetMapping("/chain")
    public String chain() {
        String inner = restClient.get().uri("/hello").retrieve().body(String.class);
        return "Chain -> " + inner;
    }

    @GetMapping("/flaky")
    public String flaky() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Intermittent failure");
        }
        return "Lucky this time";
    }
}
