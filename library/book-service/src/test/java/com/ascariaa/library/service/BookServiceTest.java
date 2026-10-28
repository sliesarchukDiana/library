package com.ascariaa.library.service;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.entity.Book;
import com.ascariaa.library.domain.mapper.BookMapper;
import com.ascariaa.library.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock private BookRepository bookRepository;
    @Mock private BookMapper bookMapper;
    @InjectMocks private BookService bookService;

    @Test
    void getAllBooks_returnsList() {
        Book book = Book.builder().id(1L).build();
        BookDto dto = new BookDto();
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(dto);
        List<BookDto> result = bookService.getAllBooks();
        assertEquals(1, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    void getBookById_existingId_returnsDto() {
        Long id = 1L;
        Book book = Book.builder().id(id).build();
        BookDto dto = new BookDto();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(dto);
        BookDto result = bookService.getBookById(id);
        assertNotNull(result);
    }

    @Test
    void getBookById_nonExistingId_throwsException() {
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookService.getBookById(id));
    }

    @Test
    void createBook_validInput_returnsDto() {
        BookCreateDto createDto = new BookCreateDto();
        Book book = Book.builder().build();
        Book savedBook = Book.builder().id(1L).build();
        BookDto expectedDto = new BookDto();
        when(bookMapper.toEntity(createDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(expectedDto);
        BookDto result = bookService.createBook(createDto);
        assertEquals(expectedDto, result);
        verify(bookRepository).save(book);
    }

    @Test
    void deleteBook_existingId_deletesSuccessfully() {
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(true);
        bookService.deleteBook(id);
        verify(bookRepository).deleteById(id);
    }

    @Test
    void deleteBook_nonExistingId_throwsEntityNotFoundException() {
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> bookService.deleteBook(id));
    }

    @Test
    void decrementCopies_success() {
        Book book = Book.builder().id(1L).availableCopies(2).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        bookService.decrementCopies(1L);
        assertEquals(1, book.getAvailableCopies());
        verify(bookRepository).save(book);
    }

    @Test
    void decrementCopies_throwsExceptionWhenZero() {
        Book book = Book.builder().id(1L).availableCopies(0).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        assertThrows(IllegalStateException.class, () -> bookService.decrementCopies(1L));
    }

    @Test
    void incrementCopies_success() {
        Book book = Book.builder().id(1L).availableCopies(1).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        bookService.incrementCopies(1L);
        assertEquals(2, book.getAvailableCopies());
        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_validInput_returnsDto() {
        Long id = 1L;
        com.ascariaa.library.domain.dto.BookCreateDto dto = new com.ascariaa.library.domain.dto.BookCreateDto();
        com.ascariaa.library.domain.entity.Book book = com.ascariaa.library.domain.entity.Book.builder().id(id).build();
        com.ascariaa.library.domain.dto.BookDto expectedDto = new com.ascariaa.library.domain.dto.BookDto();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        com.ascariaa.library.domain.dto.BookDto result = bookService.updateBook(id, dto);

        assertEquals(expectedDto, result);
        verify(bookMapper).updateEntity(book, dto);
        verify(bookRepository).save(book);
    }

    @Test
    void getBookByIdSelfInvocationDemo_returnsDto() {
        Long id = 1L;
        com.ascariaa.library.domain.entity.Book book = com.ascariaa.library.domain.entity.Book.builder().id(id).build();
        com.ascariaa.library.domain.dto.BookDto dto = new com.ascariaa.library.domain.dto.BookDto();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(dto);

        com.ascariaa.library.domain.dto.BookDto result = bookService.getBookByIdSelfInvocationDemo(id);

        assertNotNull(result);
    }
}
