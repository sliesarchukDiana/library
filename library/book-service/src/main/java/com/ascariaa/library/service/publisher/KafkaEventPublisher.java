package com.ascariaa.library.service.publisher;

import com.ascariaa.library.domain.event.BookDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookDeleted(BookDeletedEvent event) {
        log.info("Transaction committed. Sending BookDeletedEvent to Kafka: {}", event.getEventId());
        kafkaTemplate.send("book-deleted-topic", event.getBookId().toString(), event);
    }
}