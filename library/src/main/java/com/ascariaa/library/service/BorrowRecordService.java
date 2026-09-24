package com.ascariaa.library.service;

import com.ascariaa.library.dto.BorrowRecordDto;
import com.ascariaa.library.entity.Book;
import com.ascariaa.library.entity.BorrowRecord;
import com.ascariaa.library.entity.enums.BorrowStatus;
import com.ascariaa.library.mapper.BorrowRecordMapper;
import com.ascariaa.library.repository.BookRepository;
import com.ascariaa.library.repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final BorrowRecordMapper borrowRecordMapper;

    @Transactional
    public BorrowRecordDto borrowBook(Long bookId, UUID userId) {
        long activeRecords = borrowRecordRepository.countByKeycloakUserIdAndStatus(userId, BorrowStatus.ACTIVE);
        if (activeRecords >= 3) {
            throw new IllegalStateException("Limit of 3 active books exceeded");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies for this book");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        BorrowRecord record = BorrowRecord.builder()
                .book(book)
                .keycloakUserId(userId)
                .borrowDate(LocalDateTime.now())
                .status(BorrowStatus.ACTIVE)
                .build();

        return borrowRecordMapper.toDto(borrowRecordRepository.save(record));
    }

    @Transactional
    public BorrowRecordDto returnBook(Long recordId, UUID userId) {
        BorrowRecord record = borrowRecordRepository.findByIdAndKeycloakUserId(recordId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found or access denied"));

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new IllegalStateException("Book is already returned");
        }

        record.setStatus(BorrowStatus.RETURNED);
        record.setReturnDate(LocalDateTime.now());

        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        bookRepository.save(book);
        return borrowRecordMapper.toDto(borrowRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDto> getActiveRecordsByUser(UUID userId) {
        return borrowRecordRepository.findByKeycloakUserIdAndStatus(userId, BorrowStatus.ACTIVE)
                .stream()
                .map(borrowRecordMapper::toDto)
                .collect(Collectors.toList());
    }
}