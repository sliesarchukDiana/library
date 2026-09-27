package com.ascariaa.library.service;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.entity.Book;
import com.ascariaa.library.domain.enums.BorrowStatus;
import com.ascariaa.library.domain.mapper.BookMapper;
import com.ascariaa.library.repository.BookRepository;
import com.ascariaa.library.repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @InjectMocks
    private BookService bookService;

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
    void updateBook_existingId_returnsUpdatedDto() {

        Long id = 1L;
        BookCreateDto createDto = new BookCreateDto();
        Book existingBook = Book.builder().id(id).build();
        Book savedBook = Book.builder().id(id).build();
        BookDto expectedDto = new BookDto();

        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(expectedDto);

        BookDto result = bookService.updateBook(id, createDto);

        assertEquals(expectedDto, result);
        verify(bookMapper).updateEntity(existingBook, createDto);
        verify(bookRepository).save(existingBook);
    }

    @Test
    void deleteBook_existingIdNoActiveBorrows_deletesSuccessfully() {
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(true);
        when(borrowRecordRepository.existsByBookIdAndStatus(id, BorrowStatus.ACTIVE)).thenReturn(false);

        bookService.deleteBook(id);

        verify(borrowRecordRepository).deleteByBookId(id);
        verify(bookRepository).deleteById(id);
    }

    @Test
    void deleteBook_nonExistingId_throwsEntityNotFoundException() {
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookService.deleteBook(id));
    }

    @Test
    void deleteBook_activeBorrowsExist_throwsIllegalStateException() {
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(true);
        when(borrowRecordRepository.existsByBookIdAndStatus(id, BorrowStatus.ACTIVE)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> bookService.deleteBook(id));
        verify(bookRepository, never()).deleteById(any());
    }
}