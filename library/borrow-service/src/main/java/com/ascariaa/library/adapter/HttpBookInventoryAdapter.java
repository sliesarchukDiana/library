package com.ascariaa.library.adapter;

import com.ascariaa.library.domain.exception.ServiceUnavailableException;
import com.ascariaa.library.port.BookInventoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class HttpBookInventoryAdapter implements BookInventoryPort {

    private final RestClient restClient;
    private final String baseUrl = "http://book-service:8081/api/books";

    public HttpBookInventoryAdapter() {
        this.restClient = RestClient.create();
    }

    @Override
    public void decrementCopies(Long bookId, String token) {
        try {
            restClient.patch()
                    .uri(baseUrl + "/" + bookId + "/decrement")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw handleHttpException(e);
        }
    }

    @Override
    public void incrementCopies(Long bookId, String token) {
        try {
            restClient.patch()
                    .uri(baseUrl + "/" + bookId + "/increment")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw handleHttpException(e);
        }
    }

    private RuntimeException handleHttpException(Exception e) {
        log.error("HTTP call failed: {}", e.getMessage());
        return switch (e) {
            case HttpClientErrorException.NotFound notFound -> new EntityNotFoundException("Book not found via HTTP");
            case HttpClientErrorException clientError -> new IllegalStateException("Invalid request to Book service: " + clientError.getStatusCode(), e);
            case ResourceAccessException resourceAccessException -> new ServiceUnavailableException("Book service is unreachable or timed out via HTTP", e);
            case HttpServerErrorException httpServerErrorException -> new ServiceUnavailableException("Book service encountered an internal error", e);
            case RestClientException restClientException -> new ServiceUnavailableException("Unexpected HTTP communication error", e);
            default -> new ServiceUnavailableException("Unexpected error", e);
        };
    }
}