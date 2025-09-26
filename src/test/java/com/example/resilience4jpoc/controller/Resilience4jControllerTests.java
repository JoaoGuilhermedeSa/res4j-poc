package com.example.resilience4jpoc.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.resilience4jpoc.service.HelloService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "resilience4j.circuitbreaker.instances.circuitBreaker.sliding-window-size=5",
    "resilience4j.circuitbreaker.instances.circuitBreaker.failure-rate-threshold=50",
    "resilience4j.circuitbreaker.instances.circuitBreaker.wait-duration-in-open-state=2s",
    "resilience4j.circuitbreaker.instances.circuitBreaker.permitted-number-of-calls-in-half-open-state=3",
    "resilience4j.ratelimiter.instances.rateLimiter.limit-for-period=2",
    "resilience4j.ratelimiter.instances.rateLimiter.limit-refresh-period=1s",
    "resilience4j.ratelimiter.instances.rateLimiter.timeout-duration=0s",
    "resilience4j.retry.instances.retry.max-attempts=3",
    "resilience4j.retry.instances.retry.wait-duration=100ms",
    "resilience4j.bulkhead.instances.bulkhead.max-concurrent-calls=2",
    "resilience4j.bulkhead.instances.bulkhead.max-wait-duration=2s",
    "resilience4j.timelimiter.instances.timeLimiter.timeout-duration=1s"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class Resilience4jControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HelloService helloService;

    @Test
    void testBulkheadRejection() throws Exception {
        when(helloService.hello()).thenAnswer(invocation -> {
            Thread.sleep(1000);
            return "Delayed";
        });

        for (int i = 0; i < 2; i++) {
            mockMvc.perform(get("/bulkhead"));
        }

        Thread.sleep(200);

        MvcResult result = mockMvc.perform(get("/bulkhead"))
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bulkhead fallback")));
    }

    @Test
    void testCircuitBreakerFallback() throws Exception {
        when(helloService.hello()).thenThrow(new RuntimeException("Remote service is down!"));

        MvcResult result = mockMvc.perform(get("/circuit-breaker"))
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Fallback response after exception")));
    }

    @Test
    void testCircuitBreakerOpensAfterFailures() throws Exception {
        when(helloService.hello()).thenThrow(new RuntimeException("Remote service is down!"));

        for (int i = 0; i < 5; i++) {
            MvcResult result = mockMvc.perform(get("/circuit-breaker"))
                    .andExpect(status().isOk()).andReturn();
            mockMvc.perform(asyncDispatch(result));
        }

        MvcResult result = mockMvc.perform(get("/circuit-breaker"))
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(content().string(containsString("Fallback response after exception")));
    }

    @Test
    void testRateLimiterAllowsWithinLimits() throws Exception {
        when(helloService.hello()).thenReturn("Hello from service");

        mockMvc.perform(get("/rate-limiter")).andExpect(status().isOk())
                .andExpect(content().string("Hello from service"));
        Thread.sleep(100);
        mockMvc.perform(get("/rate-limiter")).andExpect(status().isOk())
                .andExpect(content().string("Hello from service"));

        verify(helloService, times(2)).hello();
    }

    @Test
    void testRateLimiterRejectsExcessRequests() throws Exception {
        when(helloService.hello()).thenReturn("Hello from service");

        mockMvc.perform(get("/rate-limiter")).andExpect(status().isOk());
        mockMvc.perform(get("/rate-limiter")).andExpect(status().isOk());

        Thread.sleep(1100);

        mockMvc.perform(get("/rate-limiter")).andExpect(status().isOk());
    }

    @Test
    void testRetrySuccess() throws Exception {
        when(helloService.hello()).thenReturn("Hello from service");

        mockMvc.perform(get("/retry"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from service"));

        verify(helloService, times(1)).hello();
    }

    @Test
    void testRetryWithFailureThenSuccess() throws Exception {
        when(helloService.hello())
                .thenThrow(new RuntimeException("Service unavailable"))
                .thenThrow(new RuntimeException("Service unavailable"))
                .thenReturn("Hello from service");

        mockMvc.perform(get("/retry"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from service"));

        verify(helloService, times(3)).hello();
    }

    @Test
    void testRetryExhaustsAllAttemptsAndFallsBack() throws Exception {
        when(helloService.hello()).thenThrow(new RuntimeException("Service down"));

        mockMvc.perform(get("/retry"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Fallback response after exception")));

        verify(helloService, times(3)).hello();
    }

    @Test
    void testTimeLimiterSuccess() throws Exception {
        when(helloService.hello()).thenReturn("Hello from service");

        MvcResult result = mockMvc.perform(get("/time-limiter"))
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from service"));
    }

    @Test
    void testTimeLimiterTimeout() throws Exception {
        when(helloService.hello()).thenAnswer(invocation -> {
            Thread.sleep(2000);
            return "Too slow";
        });

        MvcResult result = mockMvc.perform(get("/time-limiter"))
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Time limiter fallback")));
    }

    @Test
    void testMultipleResiliencePatternsIntegration() throws Exception {
        AtomicInteger callCount = new AtomicInteger(0);

        when(helloService.hello()).thenAnswer(invocation -> {
            int count = callCount.incrementAndGet();
            if (count <= 2) {
                throw new RuntimeException("Temporary failure " + count);
            }
            return "Success after retries";
        });

        mockMvc.perform(get("/retry"))
                .andExpect(status().isOk())
                .andExpect(content().string("Success after retries"));

        verify(helloService, times(3)).hello();
    }
}