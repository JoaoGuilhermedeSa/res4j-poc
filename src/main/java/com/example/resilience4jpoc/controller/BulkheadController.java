package com.example.resilience4jpoc.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resilience4jpoc.service.HelloService;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;

@RestController
public class BulkheadController {
	
	private HelloService helloService;

	@GetMapping("/bulkhead")
	@Bulkhead(name = "bulkhead", fallbackMethod = "fallback")
	public CompletableFuture<String> bulkhead() {
	    return CompletableFuture.supplyAsync(() -> {
	        try {
	            Thread.sleep(500); // Simulates a long-running task
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            throw new RuntimeException(e);
	        }
	        return helloService.hello();
	    });
	}

	public CompletableFuture<String> fallback(Throwable t) {
	    return CompletableFuture.completedFuture("Bulkhead fallback: " + t.getMessage());
	}
}
