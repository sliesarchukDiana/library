package com.ascariaa.library.adapter;

import com.ascariaa.library.domain.dto.BorrowRecordDto;
import com.ascariaa.library.domain.exception.ErrorResponse;
import com.ascariaa.library.domain.exception.ServiceUnavailableException;
import com.ascariaa.library.port.BorrowRecordPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Slf4j
@Component
public class HttpBorrowRecordAdapter implements BorrowRecordPort {

    private final RestClient restClient;
    private final String baseUrl = "http://borrow-service:8082/api/borrow";

    public HttpBorrowRecordAdapter() {
        this.restClient = RestClient.create();
    }

    @Override
    public List<BorrowRecordDto> getActiveRecords(String token) {
        try {
            return restClient.get()
                    .uri(baseUrl + "/active")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BorrowRecordDto>>() {});
        } catch (Exception e) {
            throw handleHttpException(e);
        }
    }

    @Override
    public void borrowBook(Long bookId, String token) {
        try {
            restClient.post()
                    .uri(baseUrl + "/" + bookId)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw handleHttpException(e);
        }
    }

    @Override
    public void returnBook(Long recordId, String token) {
        try {
            restClient.post()
                    .uri(baseUrl + "/return/" + recordId)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw handleHttpException(e);
        }
    }

    @Override
    public void generateReport(String token) {
        try {
            restClient.post()
                    .uri(baseUrl + "/reports/generate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw handleHttpException(e);
        }
    }

    private RuntimeException handleHttpException(Exception e) {
        log.error("HTTP call failed: {}", e.getMessage());

        if (e instanceof RestClientResponseException restException) {
            String message = restException.getMessage();
            try {
                ErrorResponse errorResponse = restException.getResponseBodyAs(ErrorResponse.class);
                if (errorResponse != null && errorResponse.message() != null) {
                    message = errorResponse.message();
                }
            } catch (Exception ignored) {}

            return switch (restException.getStatusCode().value()) {
                case 404 -> new EntityNotFoundException(message);
                case 400, 403, 409 -> new IllegalStateException(message);
                default -> new ServiceUnavailableException(message, e);
            };
        } else if (e instanceof ResourceAccessException) {
            return new ServiceUnavailableException("Borrow service is unreachable or timed out", e);
        }
        return new ServiceUnavailableException("Unexpected error communicating with Borrow service", e);
    }
}