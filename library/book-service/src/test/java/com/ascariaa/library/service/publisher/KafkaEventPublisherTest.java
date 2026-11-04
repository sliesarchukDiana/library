package com.ascariaa.library.service.publisher;

import com.ascariaa.library.domain.event.BookDeletedEvent;
import com.ascariaa.library.domain.event.BorrowConfirmedEvent;
import com.ascariaa.library.domain.event.BorrowRejectedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock private KafkaTemplate<Object, Object> kafkaTemplate;
    @InjectMocks private KafkaEventPublisher publisher;

    @Test
    void handleBookDeleted_sendsToKafka() {
        BookDeletedEvent event = new BookDeletedEvent(UUID.randomUUID(), 1L, LocalDateTime.now());
        publisher.handleBookDeleted(event);
        verify(kafkaTemplate).send("book-deleted-topic", "1", event);
    }

    @Test
    void handleBorrowConfirmed_sendsToKafka() {
        BorrowConfirmedEvent event = new BorrowConfirmedEvent(UUID.randomUUID(), 1L, "Title", UUID.randomUUID(), LocalDateTime.now());
        publisher.handleBorrowConfirmed(event);
        verify(kafkaTemplate).send("borrow-confirmed-topic", "1", event);
    }

    @Test
    void handleBorrowRejected_sendsToKafka() {
        BorrowRejectedEvent event = new BorrowRejectedEvent(UUID.randomUUID(), 1L, "Reason", LocalDateTime.now());
        publisher.handleBorrowRejected(event);
        verify(kafkaTemplate).send("borrow-rejected-topic", "1", event);
    }
}