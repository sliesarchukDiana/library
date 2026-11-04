package com.ascariaa.library.service.publisher;

import com.ascariaa.library.domain.event.BookReturnedEvent;
import com.ascariaa.library.domain.event.BorrowRequestedEvent;
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
    void handleBorrowRequested_sendsToKafka() {
        BorrowRequestedEvent event = new BorrowRequestedEvent(UUID.randomUUID(), 1L, 2L, UUID.randomUUID(), LocalDateTime.now());
        publisher.handleBorrowRequested(event);
        verify(kafkaTemplate).send("borrow-requested-topic", "1", event);
    }

    @Test
    void handleBookReturned_sendsToKafka() {
        BookReturnedEvent event = new BookReturnedEvent(UUID.randomUUID(), 2L, LocalDateTime.now());
        publisher.handleBookReturned(event);
        verify(kafkaTemplate).send("book-returned-topic", "2", event);
    }
}