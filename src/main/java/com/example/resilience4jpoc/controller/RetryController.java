package com.example.resilience4jpoc.controller;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resilience4jpoc.service.HelloService;

@RestController
public class RetryController {

    private final HelloService helloService;

    public RetryController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/retry")
    @Retry(name = "retry", fallbackMethod = "fallback")
    public String retry() {
        return helloService.hello();
    }

    public String fallback(Throwable t) {
        return "Fallback response after exception: " + t.getMessage();
    }
}
