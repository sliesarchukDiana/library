package com.ascariaa.library.domain.mapper;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.entity.Book;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookMapperTest {

    private final BookMapper bookMapper = new BookMapperImpl();

    @Test
    void toDto_validEntity_returnsDto() {
        Book book = Book.builder().id(1L).title("1984").author("Orwell").availableCopies(5).build();
        BookDto dto = bookMapper.toDto(book);
        assertEquals(1L, dto.getId());
        assertEquals("1984", dto.getTitle());
    }

    @Test
    void toEntity_validDto_returnsEntity() {
        BookCreateDto dto = new BookCreateDto();
        dto.setTitle("1984");
        dto.setAuthor("Orwell");
        Book book = bookMapper.toEntity(dto);
        assertEquals("1984", book.getTitle());
    }
}
