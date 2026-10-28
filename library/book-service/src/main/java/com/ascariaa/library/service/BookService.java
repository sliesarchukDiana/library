package com.ascariaa.library.service;

import com.ascariaa.library.domain.annotation.CacheResult;
import com.ascariaa.library.domain.annotation.RateLimit;
import com.ascariaa.library.domain.annotation.RetryOperation;
import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.entity.Book;
import com.ascariaa.library.domain.mapper.BookMapper;
import com.ascariaa.library.repository.BookRepository;
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
        bookRepository.deleteById(id);
    }

    protected Book findBookEntity(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    @Transactional
    public void decrementCopies(Long id) {
        Book book = findBookEntity(id);
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }

    @Transactional
    public void incrementCopies(Long id) {
        Book book = findBookEntity(id);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }
}