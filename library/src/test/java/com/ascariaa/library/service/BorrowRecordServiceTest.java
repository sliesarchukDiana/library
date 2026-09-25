package com.ascariaa.library.service;

import com.ascariaa.library.dto.BorrowRecordDto;
import com.ascariaa.library.entity.Book;
import com.ascariaa.library.entity.BorrowRecord;
import com.ascariaa.library.entity.enums.BorrowStatus;
import com.ascariaa.library.mapper.BorrowRecordMapper;
import com.ascariaa.library.repository.BookRepository;
import com.ascariaa.library.repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowRecordServiceTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @InjectMocks
    private BorrowRecordService borrowRecordService;


    @Test
    void borrowBook_validInput_returnsDto() {
        Long bookId = 1L;
        UUID userId = UUID.randomUUID();
        Book book = Book.builder().id(bookId).availableCopies(2).build();
        BorrowRecord savedRecord = BorrowRecord.builder().status(BorrowStatus.ACTIVE).build();
        BorrowRecordDto dto = new BorrowRecordDto();

        when(borrowRecordRepository.countByKeycloakUserIdAndStatus(userId, BorrowStatus.ACTIVE)).thenReturn(2L);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(savedRecord);
        when(borrowRecordMapper.toDto(savedRecord)).thenReturn(dto);

        BorrowRecordDto result = borrowRecordService.borrowBook(bookId, userId);

        assertNotNull(result);
        assertEquals(1, book.getAvailableCopies());

        verify(bookRepository).save(book);
        ArgumentCaptor<BorrowRecord> recordCaptor = ArgumentCaptor.forClass(BorrowRecord.class);
        verify(borrowRecordRepository).save(recordCaptor.capture());

        BorrowRecord capturedRecord = recordCaptor.getValue();
        assertEquals(BorrowStatus.ACTIVE, capturedRecord.getStatus());
        assertEquals(userId, capturedRecord.getKeycloakUserId());
        assertNotNull(capturedRecord.getBorrowDate());
    }

    @Test
    void borrowBook_limitExceeded_throwsIllegalStateException() {
        Long bookId = 1L;
        UUID userId = UUID.randomUUID();
        when(borrowRecordRepository.countByKeycloakUserIdAndStatus(userId, BorrowStatus.ACTIVE)).thenReturn(3L);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> borrowRecordService.borrowBook(bookId, userId));
        assertEquals("Limit of 3 active books exceeded", exception.getMessage());
        verify(bookRepository, never()).findById(any());
    }

    @Test
    void borrowBook_noAvailableCopies_throwsIllegalStateException() {
        Long bookId = 1L;
        UUID userId = UUID.randomUUID();
        Book book = Book.builder().id(bookId).availableCopies(0).build();

        when(borrowRecordRepository.countByKeycloakUserIdAndStatus(userId, BorrowStatus.ACTIVE)).thenReturn(1L);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertThrows(IllegalStateException.class, () -> borrowRecordService.borrowBook(bookId, userId)); // AC2
    }

    @Test
    void borrowBook_bookNotFound_throwsEntityNotFoundException() {
        Long bookId = 1L;
        UUID userId = UUID.randomUUID();
        when(borrowRecordRepository.countByKeycloakUserIdAndStatus(userId, BorrowStatus.ACTIVE)).thenReturn(1L);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> borrowRecordService.borrowBook(bookId, userId)); // AC2
    }

    @Test
    void returnBook_validInput_returnsDto() {
        Long recordId = 1L;
        UUID userId = UUID.randomUUID();
        Book book = Book.builder().id(1L).availableCopies(1).build();
        BorrowRecord record = BorrowRecord.builder()
                .id(recordId)
                .book(book)
                .status(BorrowStatus.ACTIVE)
                .build();
        BorrowRecordDto dto = new BorrowRecordDto();

        when(borrowRecordRepository.findByIdAndKeycloakUserId(recordId, userId)).thenReturn(Optional.of(record));
        when(borrowRecordRepository.save(record)).thenReturn(record);
        when(borrowRecordMapper.toDto(record)).thenReturn(dto);

        BorrowRecordDto result = borrowRecordService.returnBook(recordId, userId);

        assertNotNull(result);
        assertEquals(BorrowStatus.RETURNED, record.getStatus());
        assertNotNull(record.getReturnDate());
        assertEquals(2, book.getAvailableCopies());

        verify(bookRepository).save(book);
        verify(borrowRecordRepository).save(record);
    }

    @Test
    void returnBook_recordNotFound_throwsEntityNotFoundException() {
        Long recordId = 1L;
        UUID userId = UUID.randomUUID();
        when(borrowRecordRepository.findByIdAndKeycloakUserId(recordId, userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> borrowRecordService.returnBook(recordId, userId));
    }

    @Test
    void returnBook_alreadyReturned_throwsIllegalStateException() {
        Long recordId = 1L;
        UUID userId = UUID.randomUUID();
        BorrowRecord record = BorrowRecord.builder().status(BorrowStatus.RETURNED).build();
        when(borrowRecordRepository.findByIdAndKeycloakUserId(recordId, userId)).thenReturn(Optional.of(record));


        assertThrows(IllegalStateException.class, () -> borrowRecordService.returnBook(recordId, userId)); // AC2
    }

    @Test
    void getActiveRecordsByUser_validInput_returnsList() {
        UUID userId = UUID.randomUUID();
        BorrowRecord record = BorrowRecord.builder().id(1L).build();
        BorrowRecordDto dto = new BorrowRecordDto();

        when(borrowRecordRepository.findByKeycloakUserIdAndStatus(userId, BorrowStatus.ACTIVE))
                .thenReturn(List.of(record));
        when(borrowRecordMapper.toDto(record)).thenReturn(dto);

        List<BorrowRecordDto> result = borrowRecordService.getActiveRecordsByUser(userId);

        assertEquals(1, result.size());
        verify(borrowRecordRepository).findByKeycloakUserIdAndStatus(userId, BorrowStatus.ACTIVE); // AC7
    }
}