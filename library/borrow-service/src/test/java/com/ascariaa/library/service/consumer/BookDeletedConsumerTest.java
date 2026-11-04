package com.ascariaa.library.service.consumer;

import com.ascariaa.library.domain.event.BookDeletedEvent;
import com.ascariaa.library.repository.BorrowRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookDeletedConsumerTest {
    @Mock private BorrowRecordRepository borrowRecordRepository;
    @InjectMocks private BookDeletedConsumer consumer;

    @Test
    void consumeBookDeleted_idempotencyCheck() {
        BookDeletedEvent event = new BookDeletedEvent(UUID.randomUUID(), 1L, LocalDateTime.now());

        consumer.consumeBookDeleted(event);
        consumer.consumeBookDeleted(event);

        verify(borrowRecordRepository, times(2)).deleteByBookId(1L);
    }
}