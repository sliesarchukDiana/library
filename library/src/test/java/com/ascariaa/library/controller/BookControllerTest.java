package com.ascariaa.library.controller;

import com.ascariaa.library.dto.BookCreateDto;
import com.ascariaa.library.dto.BookDto;
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

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

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
    void createBook_returnsDto() {
        BookCreateDto createDto = new BookCreateDto();
        BookDto expected = new BookDto();
        when(bookService.createBook(createDto)).thenReturn(expected);

        BookDto actual = bookController.createBook(createDto);

        assertEquals(expected, actual);
    }

    @Test
    void updateBook_returnsDto() {
        Long id = 1L;
        BookCreateDto createDto = new BookCreateDto();
        BookDto expected = new BookDto();
        when(bookService.updateBook(id, createDto)).thenReturn(expected);

        BookDto actual = bookController.updateBook(id, createDto);

        assertEquals(expected, actual);
    }

    @Test
    void deleteBook_callsService() {
        Long id = 1L;

        bookController.deleteBook(id);

        verify(bookService).deleteBook(id);
    }
}