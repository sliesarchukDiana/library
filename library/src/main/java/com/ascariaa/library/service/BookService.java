package com.ascariaa.library.service;

import com.ascariaa.library.dto.BookCreateDto;
import com.ascariaa.library.dto.BookDto;
import com.ascariaa.library.entity.Book;
import com.ascariaa.library.mapper.BookMapper;
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

    @Transactional(readOnly = true)
    public BookDto getBookById(Long id) {
        return bookMapper.toDto(findBookEntity(id));
    }

    @Transactional
    public BookDto createBook(BookCreateDto dto) {
        Book book = bookMapper.toEntity(dto);
        return bookMapper.toDto(bookRepository.save(book));
    }

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
}