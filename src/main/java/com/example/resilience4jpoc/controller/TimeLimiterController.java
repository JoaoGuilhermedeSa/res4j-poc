package com.example.resilience4jpoc.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resilience4jpoc.service.HelloService;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RestController
public class TimeLimiterController {

    private final HelloService helloService;

    public TimeLimiterController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/time-limiter")
    @TimeLimiter(name = "timeLimiter", fallbackMethod = "fallback")
    public CompletableFuture<String> timeLimiter() {
        return CompletableFuture.supplyAsync(() -> helloService.hello());
    }
    
    private CompletableFuture<String> fallback(Throwable t) {
        return CompletableFuture.completedFuture("Time limiter fallback");
    }
}
