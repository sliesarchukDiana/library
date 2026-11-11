package com.ascariaa.library.client;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface BookHttpClient {
    @GetExchange("/api/books")
    List<BookDto> getAllBooks(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PostExchange("/api/books")
    void createBook(@RequestBody BookCreateDto dto, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @DeleteExchange("/api/books/{id}")
    void deleteBook(@PathVariable("id") Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}