package com.ascariaa.library.controller;

import com.ascariaa.library.domain.dto.BorrowRecordDto;
import com.ascariaa.library.service.BorrowRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowRecordControllerTest {

    @Mock
    private BorrowRecordService borrowRecordService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private BorrowRecordController borrowRecordController;

    @Test
    void borrowBook_callsService_returnsDto() {
        Long bookId = 1L;
        UUID userId = UUID.randomUUID();
        BorrowRecordDto expectedDto = new BorrowRecordDto();

        when(borrowRecordService.borrowBook(bookId, userId)).thenReturn(expectedDto);

        BorrowRecordDto actualDto = borrowRecordController.borrowBook(bookId, userId);

        assertEquals(expectedDto, actualDto);
        verify(borrowRecordService).borrowBook(bookId, userId);
    }

    @Test
    void returnBook_callsService_returnsDto() {
        Long recordId = 1L;
        UUID userId = UUID.randomUUID();
        BorrowRecordDto expected = new BorrowRecordDto();

        when(jwt.getSubject()).thenReturn(userId.toString());
        when(borrowRecordService.returnBook(recordId, userId)).thenReturn(expected);

        BorrowRecordDto actual = borrowRecordController.returnBook(recordId, jwt);

        assertEquals(expected, actual);
        verify(borrowRecordService).returnBook(recordId, userId);
    }
}