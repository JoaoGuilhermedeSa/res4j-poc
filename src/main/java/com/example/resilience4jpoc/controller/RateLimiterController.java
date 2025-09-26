package com.example.resilience4jpoc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resilience4jpoc.service.HelloService;

@RestController
public class RateLimiterController {

    private final HelloService helloService;

    public RateLimiterController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/rate-limiter")
    @RateLimiter(name = "rateLimiter")
    public String rateLimiter() {
        return helloService.hello();
    }
}
