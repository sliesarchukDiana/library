package com.ascariaa.library.service.publisher;

import com.ascariaa.library.domain.event.BookDeletedEvent;
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

}