package com.ascariaa.library.domain.mapper;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.entity.Book;
import com.ascariaa.library.domain.mapper.BookMapper;
import com.ascariaa.library.domain.mapper.BookMapperImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookMapperTest {

    private final BookMapper bookMapper = new BookMapperImpl();

    @Test
    void toDto_validEntity_returnsDto() {
        Book book = Book.builder().id(1L).title("1984").author("Orwell").availableCopies(5).build();
        BookDto dto = bookMapper.toDto(book);

        assertEquals(1L, dto.getId());
        assertEquals("1984", dto.getTitle());
        assertEquals("Orwell", dto.getAuthor());
        assertEquals(5, dto.getAvailableCopies());
    }

    @Test
    void toDto_nullEntity_returnsNull() {
        assertNull(bookMapper.toDto(null));
    }

    @Test
    void toEntity_validDto_returnsEntity() {
        BookCreateDto dto = new BookCreateDto();
        dto.setTitle("1984");
        dto.setAuthor("Orwell");
        dto.setAvailableCopies(5);

        Book book = bookMapper.toEntity(dto);

        assertNull(book.getId());
        assertEquals("1984", book.getTitle());
        assertEquals("Orwell", book.getAuthor());
        assertEquals(5, book.getAvailableCopies());
    }

    @Test
    void updateEntity_validDto_updatesEntity() {
        Book book = Book.builder().title("Old").author("Old").availableCopies(1).build();
        BookCreateDto dto = new BookCreateDto();
        dto.setTitle("New");
        dto.setAuthor("New");
        dto.setAvailableCopies(10);

        bookMapper.updateEntity(book, dto);

        assertEquals("New", book.getTitle());
        assertEquals("New", book.getAuthor());
        assertEquals(10, book.getAvailableCopies());
    }
}