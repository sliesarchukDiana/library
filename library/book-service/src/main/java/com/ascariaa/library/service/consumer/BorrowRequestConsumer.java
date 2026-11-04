package com.ascariaa.library.service.consumer;

import com.ascariaa.library.domain.entity.ProcessedEvent;
import com.ascariaa.library.domain.event.BorrowConfirmedEvent;
import com.ascariaa.library.domain.event.BorrowRejectedEvent;
import com.ascariaa.library.domain.event.BorrowRequestedEvent;
import com.ascariaa.library.repository.BookRepository;
import com.ascariaa.library.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BorrowRequestConsumer {
    private final BookRepository bookRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = "borrow-requested-topic")
    @Transactional
    public void consumeBorrowRequest(BorrowRequestedEvent event) {
        if (processedEventRepository.existsById(event.getEventId())) return;

        bookRepository.findById(event.getBookId()).ifPresentOrElse(
                book -> {
                    if (book.getAvailableCopies() > 0) {
                        book.setAvailableCopies(book.getAvailableCopies() - 1);
                        bookRepository.save(book);
                        eventPublisher.publishEvent(new BorrowConfirmedEvent(UUID.randomUUID(), event.getRecordId(), book.getTitle(), event.getUserId(), LocalDateTime.now()));
                    } else {
                        eventPublisher.publishEvent(new BorrowRejectedEvent(UUID.randomUUID(), event.getRecordId(), "No available copies", LocalDateTime.now()));
                    }
                },
                () -> eventPublisher.publishEvent(new BorrowRejectedEvent(UUID.randomUUID(), event.getRecordId(), "Book not found", LocalDateTime.now()))
        );

        processedEventRepository.save(new ProcessedEvent(event.getEventId(), LocalDateTime.now()));
    }
}