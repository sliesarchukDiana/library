package com.ascariaa.library.service.publisher;

import com.ascariaa.library.domain.event.BookBorrowedEvent;
import com.ascariaa.library.domain.event.BookReturnedEvent;
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
    public void handleBookBorrowed(BookBorrowedEvent event) {
        log.info("Transaction committed. Sending BookBorrowedEvent to Kafka: {}", event.getEventId());
        kafkaTemplate.send("book-borrowed-topic", event.getBookId().toString(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookReturned(BookReturnedEvent event) {
        log.info("Transaction committed. Sending BookReturnedEvent to Kafka: {}", event.getEventId());
        kafkaTemplate.send("book-returned-topic", event.getBookId().toString(), event);
    }
}