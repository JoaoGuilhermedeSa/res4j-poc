# Resilience4j Proof of Concept

This project is a proof-of-concept application demonstrating the use of various Resilience4j modules in a Spring Boot application.

## Features

This application showcases the following Resilience4j modules:

*   **Circuit Breaker**: Prevents cascading failures by stopping requests to a failing service.
*   **Rate Limiter**: Limits the number of requests to a service within a given time period.
*   **Bulkhead**: Limits the number of concurrent requests to a service.
*   **Retry**: Automatically retries a failed operation.
*   **Time Limiter**: Sets a timeout for an operation.

## Endpoint

The application exposes a single endpoint:

*   `GET /hello`: This endpoint simulates a call to a remote service that may fail. It is protected by the Resilience4j modules listed above.

## Running the Application

To run the application, execute the following command in the project's root directory:

```bash
mvn spring-boot:run
```

The application will start on port 8081.

## Testing the Resilience4j Features

You can test the different Resilience4j features by sending requests to the `/hello` endpoint.

*   **Circuit Breaker**: Send multiple requests to the `/hello` endpoint. If the failure rate exceeds 50%, the circuit breaker will open, and subsequent requests will fail fast with a `CallNotPermittedException`.
*   **Rate Limiter**: Send more than 5 requests to the `/hello` endpoint within 1 second. The excess requests will be rejected with a `RequestNotPermitted` exception.
*   **Bulkhead**: Send more than 10 concurrent requests to the `/hello` endpoint. The excess requests will be rejected.
*   **Retry**: If a request to the `/hello` endpoint fails, it will be automatically retried up to 3 times.
*   **Time Limiter**: If a request to the `/hello` endpoint takes longer than 2 seconds, it will be timed out.
