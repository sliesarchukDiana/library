package com.ascariaa.library.controller;

import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {
    @Mock private BookService bookService;
    @InjectMocks private BookController bookController;

    @Test
    void getAllBooks_returnsList() {
        List<BookDto> expected = List.of(new BookDto());
        when(bookService.getAllBooks()).thenReturn(expected);
        List<BookDto> actual = bookController.getAllBooks();
        assertEquals(expected, actual);
        verify(bookService).getAllBooks();
    }

    @Test
    void getBookById_returnsDto() {
        Long id = 1L;
        BookDto expected = new BookDto();
        when(bookService.getBookById(id)).thenReturn(expected);
        BookDto actual = bookController.getBookById(id);
        assertEquals(expected, actual);
    }

    @Test
    void decrementCopies_callsService() {
        bookController.decrementCopies(1L);
        verify(bookService).decrementCopies(1L);
    }

    @Test
    void incrementCopies_callsService() {
        bookController.incrementCopies(1L);
        verify(bookService).incrementCopies(1L);
    }

    @Test
    void createBook_callsService() {
        com.ascariaa.library.domain.dto.BookCreateDto dto = new com.ascariaa.library.domain.dto.BookCreateDto();
        BookDto expected = new BookDto();
        when(bookService.createBook(dto)).thenReturn(expected);

        BookDto actual = bookController.createBook(dto);

        assertEquals(expected, actual);
        verify(bookService).createBook(dto);
    }

    @Test
    void updateBook_callsService() {
        Long id = 1L;
        com.ascariaa.library.domain.dto.BookCreateDto dto = new com.ascariaa.library.domain.dto.BookCreateDto();
        BookDto expected = new BookDto();
        when(bookService.updateBook(id, dto)).thenReturn(expected);

        BookDto actual = bookController.updateBook(id, dto);

        assertEquals(expected, actual);
        verify(bookService).updateBook(id, dto);
    }

    @Test
    void deleteBook_callsService() {
        bookController.deleteBook(1L);
        verify(bookService).deleteBook(1L);
    }
}
