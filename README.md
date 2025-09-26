# Resilience4j Proof of Concept

This project is a proof-of-concept application demonstrating the use of various Resilience4j modules in a Spring Boot application.

## Features

This application showcases the following Resilience4j modules:

*   **Circuit Breaker**: Prevents cascading failures by stopping requests to a failing service.
*   **Rate Limiter**: Limits the number of requests to a service within a given time period.
*   **Bulkhead**: Limits the number of concurrent requests to a service.
*   **Retry**: Automatically retries a failed operation.
*   **Time Limiter**: Sets a timeout for an operation.

## How to build

To build the application, execute the following command in the project's root directory:

```bash
mvn clean install
```

## Running the Application

To run the application, execute the following command in the project's root directory:

```bash
mvn spring-boot:run
```

The application will start on port 8080.

## Endpoints

The application exposes the following endpoints:

*   `GET /circuit-breaker`: Demonstrates the Circuit Breaker pattern.
*   `GET /rate-limiter`: Demonstrates the Rate Limiter pattern.
*   `GET /bulkhead`: Demonstrates the Bulkhead pattern.
*   `GET /retry`: Demonstrates the Retry pattern.
*   `GET /time-limiter`: Demonstrates the Time Limiter pattern.

## Configuration

The Resilience4j patterns are configured in the `src/main/resources/application.properties` file:

```properties
resilience4j.circuitbreaker.instances.circuitBreaker.sliding-window-size=5
resilience4j.circuitbreaker.instances.circuitBreaker.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.circuitBreaker.wait-duration-in-open-state=2s
resilience4j.circuitbreaker.instances.circuitBreaker.permitted-number-of-calls-in-half-open-state=3

resilience4j.ratelimiter.instances.rateLimiter.limit-for-period=2
resilience4j.ratelimiter.instances.rateLimiter.limit-refresh-period=1s
resilience4j.ratelimiter.instances.rateLimiter.timeout-duration=0s

resilience4j.retry.instances.retry.max-attempts=3
resilience4j.retry.instances.retry.wait-duration=100ms

resilience4j.bulkhead.instances.bulkhead.max-concurrent-calls=2
resilience4j.bulkhead.instances.bulkhead.max-wait-duration=0s

resilience4j.timelimiter.instances.timeLimiter.timeout-duration=1s
```

## Testing the Resilience4j Features

You can test the different Resilience4j features by sending requests to the respective endpoints.

*   **Circuit Breaker**: Send multiple requests to the `/circuit-breaker` endpoint. If the failure rate exceeds 50% in a sliding window of 5 requests, the circuit breaker will open, and subsequent requests will fail fast. After 2 seconds, the circuit breaker will transition to half-open, allowing a few requests to check if the service has recovered.
*   **Rate Limiter**: Send more than 2 requests to the `/rate-limiter` endpoint within 1 second. The excess requests will be rejected with a `RequestNotPermitted` exception.
*   **Bulkhead**: Send more than 2 concurrent requests to the `/bulkhead` endpoint. The excess requests will be rejected.
*   **Retry**: If a request to the `/retry` endpoint fails, it will be automatically retried up to 3 times with a wait duration of 100ms between retries.
*   **Time Limiter**: If a request to the `/time-limiter` endpoint takes longer than 1 second, it will be timed out.