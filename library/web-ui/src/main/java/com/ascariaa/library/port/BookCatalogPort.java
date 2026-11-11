package com.ascariaa.library.port;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import java.util.List;

public interface BookCatalogPort {
    List<BookDto> getAllBooks(String token);
    void createBook(BookCreateDto dto, String token);
    void deleteBook(Long id, String token);
}