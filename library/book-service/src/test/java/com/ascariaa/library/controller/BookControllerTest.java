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
}
