package com.ascariaa.library.service;

import com.ascariaa.library.domain.entity.BorrowRecord;
import com.ascariaa.library.domain.enums.BorrowStatus;
import com.ascariaa.library.domain.mapper.BorrowRecordMapper;
import com.ascariaa.library.repository.BorrowRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowRecordServiceTest {
    @Mock private BorrowRecordRepository borrowRecordRepository;
    @Mock private BorrowRecordMapper borrowRecordMapper;
    @Mock private AsyncLibraryService asyncLibraryService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private RestClient restClient;
    @InjectMocks private BorrowRecordService borrowRecordService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(borrowRecordService, "restClient", restClient);
    }

    @Test
    void borrowBook_limitExceeded_throwsIllegalStateException() {
        when(borrowRecordRepository.countByKeycloakUserIdAndStatus(any(), any())).thenReturn(3L);
        assertThrows(IllegalStateException.class, () -> borrowRecordService.borrowBook(1L, UUID.randomUUID()));
    }

    @Test
    void returnBook_recordNotFound_throwsException() {
        when(borrowRecordRepository.findByIdAndKeycloakUserId(anyLong(), any())).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> borrowRecordService.returnBook(1L, UUID.randomUUID()));
    }

    @Test
    void returnBook_alreadyReturned_throwsException() {
        BorrowRecord record = BorrowRecord.builder().status(BorrowStatus.RETURNED).build();
        when(borrowRecordRepository.findByIdAndKeycloakUserId(anyLong(), any())).thenReturn(Optional.of(record));
        assertThrows(IllegalStateException.class, () -> borrowRecordService.returnBook(1L, UUID.randomUUID()));
    }
}
