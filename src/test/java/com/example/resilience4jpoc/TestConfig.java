package com.example.resilience4jpoc;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.example.resilience4jpoc.service.HelloService;

@TestConfiguration
class TestConfig {

	@Bean
	@Primary
	public HelloService helloService() {
		return mock(HelloService.class);
	}
}
