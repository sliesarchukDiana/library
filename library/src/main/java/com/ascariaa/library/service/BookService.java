package com.ascariaa.library.service;

import com.ascariaa.library.domain.annotation.CacheResult;
import com.ascariaa.library.domain.annotation.RateLimit;
import com.ascariaa.library.domain.annotation.RetryOperation;
import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.entity.Book;
import com.ascariaa.library.domain.enums.BorrowStatus;
import com.ascariaa.library.domain.mapper.BookMapper;
import com.ascariaa.library.repository.BookRepository;
import com.ascariaa.library.repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BorrowRecordRepository borrowRecordRepository;

    @Transactional(readOnly = true)
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @CacheResult
    @Transactional(readOnly = true)
    public BookDto getBookById(Long id) {
        return bookMapper.toDto(findBookEntity(id));
    }

    public BookDto getBookByIdSelfInvocationDemo(Long id) {
        return this.getBookById(id);
    }

    @RateLimit(minIntervalMillis = 2000)
    @Transactional
    public BookDto createBook(BookCreateDto dto) {
        Book book = bookMapper.toEntity(dto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @RetryOperation(maxAttempts = 3)
    @Transactional
    public BookDto updateBook(Long id, BookCreateDto dto) {
        Book book = findBookEntity(id);
        bookMapper.updateEntity(book, dto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found");
        }
        if (borrowRecordRepository.existsByBookIdAndStatus(id, BorrowStatus.ACTIVE)) {
            throw new IllegalStateException("Cannot delete book: it is currently borrowed by a user.");
        }
        borrowRecordRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    protected Book findBookEntity(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }
}