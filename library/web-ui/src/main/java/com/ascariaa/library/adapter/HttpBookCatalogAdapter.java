package com.ascariaa.library.adapter;

import com.ascariaa.library.client.BookHttpClient;
import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.exception.ServiceUnavailableException;
import com.ascariaa.library.port.BookCatalogPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HttpBookCatalogAdapter implements BookCatalogPort {

    private final BookHttpClient httpClient;

    @Override
    public List<BookDto> getAllBooks(String token) {
        try {
            return httpClient.getAllBooks(token);
        } catch (Exception e) {
            throw handleHttpException(e);
        }
    }

    @Override
    public void createBook(BookCreateDto dto, String token) {
        try {
            httpClient.createBook(dto, token);
        } catch (Exception e) {
            throw handleHttpException(e);
        }
    }

    @Override
    public void deleteBook(Long id, String token) {
        try {
            httpClient.deleteBook(id, token);
        } catch (Exception e) {
            throw handleHttpException(e);
        }
    }

    private RuntimeException handleHttpException(Exception e) {
        log.error("HTTP call failed: {}", e.getMessage());
        if (e instanceof HttpClientErrorException.NotFound) {
            return new EntityNotFoundException("Book not found via HTTP");
        }
        if (e instanceof RestClientException) {
            return new ServiceUnavailableException("Book service is unavailable via HTTP", e);
        }
        return new ServiceUnavailableException("Unexpected HTTP error", e);
    }
}