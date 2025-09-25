package com.example.resilience4jpoc;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io
.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    @CircuitBreaker(name = "hello", fallbackMethod = "fallback")
    @RateLimiter(name = "hello")
    @Bulkhead(name = "hello")
    @Retry(name = "hello", fallbackMethod = "fallback")
    @TimeLimiter(name = "hello")
    public CompletableFuture<String> hello() {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate a call to a remote service
            if (Math.random() > 0.5) {
                throw new RuntimeException("Remote service is down!");
            }
            return "Hello, World!";
        });
    }

    public CompletableFuture<String> fallback(Throwable t) {
        return CompletableFuture.supplyAsync(() -> "Fallback response after exception: " + t.getMessage());
    }
}
