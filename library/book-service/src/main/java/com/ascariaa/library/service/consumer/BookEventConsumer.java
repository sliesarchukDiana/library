package com.ascariaa.library.service.consumer;

import com.ascariaa.library.domain.entity.ProcessedEvent;
import com.ascariaa.library.domain.event.BookReturnedEvent;
import com.ascariaa.library.repository.ProcessedEventRepository;
import com.ascariaa.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookEventConsumer {
    private final BookService bookService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "book-returned-topic")
    @Transactional
    public void consumeBookReturned(BookReturnedEvent event) {
        if (processedEventRepository.existsById(event.getEventId())) {
            log.info("Returning is {} already processed. Skipping.", event.getEventId());
            return;
        }

        log.info("Processing BookReturnedEvent for book: {}", event.getBookId());
        bookService.incrementCopies(event.getBookId());

        processedEventRepository.save(new ProcessedEvent(event.getEventId(), LocalDateTime.now()));
    }
}