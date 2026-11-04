package com.ascariaa.library.service.publisher;

import com.ascariaa.library.domain.event.BookDeletedEvent;
import com.ascariaa.library.domain.event.BorrowConfirmedEvent;
import com.ascariaa.library.domain.event.BorrowRejectedEvent;
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBorrowConfirmed(BorrowConfirmedEvent event) {
        kafkaTemplate.send("borrow-confirmed-topic", event.getRecordId().toString(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBorrowRejected(BorrowRejectedEvent event) {
        kafkaTemplate.send("borrow-rejected-topic", event.getRecordId().toString(), event);
    }
}