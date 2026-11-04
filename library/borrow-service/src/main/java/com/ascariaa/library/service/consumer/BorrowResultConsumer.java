package com.ascariaa.library.service.consumer;

import com.ascariaa.library.domain.enums.BorrowStatus;
import com.ascariaa.library.domain.event.BorrowConfirmedEvent;
import com.ascariaa.library.domain.event.BorrowRejectedEvent;
import com.ascariaa.library.repository.BorrowRecordRepository;
import com.ascariaa.library.service.AsyncLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BorrowResultConsumer {
    private final BorrowRecordRepository borrowRecordRepository;
    private final AsyncLibraryService asyncLibraryService;

    @KafkaListener(topics = "borrow-confirmed-topic")
    @Transactional
    public void consumeConfirmed(BorrowConfirmedEvent event) {
        borrowRecordRepository.findById(event.getRecordId()).ifPresent(record -> {
            if (record.getStatus() != BorrowStatus.ACTIVE) {
                record.setStatus(BorrowStatus.ACTIVE);
                borrowRecordRepository.save(record);
                asyncLibraryService.sendBookBorrowedNotification(event.getUserId(), event.getBookTitle(), record.getBorrowDate());
            }
        });
    }

    @KafkaListener(topics = "borrow-rejected-topic")
    @Transactional
    public void consumeRejected(BorrowRejectedEvent event) {
        borrowRecordRepository.findById(event.getRecordId()).ifPresent(record -> {
            if (record.getStatus() != BorrowStatus.REJECTED) {
                record.setStatus(BorrowStatus.REJECTED);
                borrowRecordRepository.save(record);
            }
        });
    }
}