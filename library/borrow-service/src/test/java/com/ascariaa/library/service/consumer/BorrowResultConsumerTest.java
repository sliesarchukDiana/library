package com.ascariaa.library.service.consumer;

import com.ascariaa.library.domain.entity.BorrowRecord;
import com.ascariaa.library.domain.enums.BorrowStatus;
import com.ascariaa.library.domain.event.BorrowConfirmedEvent;
import com.ascariaa.library.domain.event.BorrowRejectedEvent;
import com.ascariaa.library.repository.BorrowRecordRepository;
import com.ascariaa.library.service.AsyncLibraryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowResultConsumerTest {

    @Mock private BorrowRecordRepository borrowRecordRepository;
    @Mock private AsyncLibraryService asyncLibraryService;

    @InjectMocks private BorrowResultConsumer consumer;

    @Test
    void consumeConfirmed_updatesStatusAndSendsNotification() {
        UUID userId = UUID.randomUUID();
        BorrowConfirmedEvent event = new BorrowConfirmedEvent(UUID.randomUUID(), 1L, "Title", userId, LocalDateTime.now());
        BorrowRecord record = BorrowRecord.builder().id(1L).status(BorrowStatus.PENDING).build();

        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        consumer.consumeConfirmed(event);

        verify(borrowRecordRepository).save(record);
        verify(asyncLibraryService).sendBookBorrowedNotification(eq(userId), eq("Title"), any());
    }

    @Test
    void consumeRejected_updatesStatus() {
        BorrowRejectedEvent event = new BorrowRejectedEvent(UUID.randomUUID(), 1L, "No copies", LocalDateTime.now());
        BorrowRecord record = BorrowRecord.builder().id(1L).status(BorrowStatus.PENDING).build();

        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        consumer.consumeRejected(event);

        verify(borrowRecordRepository).save(record);
    }
}