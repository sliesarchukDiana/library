package com.ascariaa.library.controller;

import com.ascariaa.library.service.BorrowRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowRecordControllerTest {
    @Mock private BorrowRecordService borrowRecordService;
    @Mock private Jwt jwt;
    @InjectMocks private BorrowRecordController borrowRecordController;

    @Test
    void borrowBook_callsService() {
        Long bookId = 1L;
        UUID userId = UUID.randomUUID();
        borrowRecordController.borrowBook(bookId, userId);
        verify(borrowRecordService).borrowBook(bookId, userId);
    }

    @Test
    void getActiveRecords_callsService() {
        UUID userId = UUID.randomUUID();
        when(borrowRecordService.getActiveRecordsByUser(userId)).thenReturn(List.of());
        borrowRecordController.getActiveRecords(userId);
        verify(borrowRecordService).getActiveRecordsByUser(userId);
    }
}
