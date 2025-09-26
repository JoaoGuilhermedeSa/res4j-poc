package com.example.resilience4jpoc.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resilience4jpoc.service.HelloService;

import java.util.concurrent.CompletableFuture;

@RestController
public class CircuitBreakerController {

    private final HelloService helloService;

    public CircuitBreakerController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/circuit-breaker")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "fallback")
    public CompletableFuture<String> circuitBreaker() {
        return CompletableFuture.supplyAsync(() -> helloService.hello());
    }

    public CompletableFuture<String> fallback(Throwable t) {
        return CompletableFuture.completedFuture("Fallback response after exception: " + t.getMessage());
    }
}
