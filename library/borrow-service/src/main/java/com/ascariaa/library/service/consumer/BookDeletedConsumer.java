package com.ascariaa.library.service.consumer;

import com.ascariaa.library.domain.event.BookDeletedEvent;
import com.ascariaa.library.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookDeletedConsumer {

    private final BorrowRecordRepository borrowRecordRepository;

    @KafkaListener(topics = "book-deleted-topic")
    @Transactional
    public void consumeBookDeleted(BookDeletedEvent event) {
        log.info("Processing BookDeletedEvent for book: {}. Deleting related borrow records.", event.getBookId());
        borrowRecordRepository.deleteByBookId(event.getBookId());
    }
}