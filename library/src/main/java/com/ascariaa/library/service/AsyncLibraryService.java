package com.ascariaa.library.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AsyncLibraryService {

    @Async
    public void sendNotification(Long recordId, String email) {
        log.info("Sending email to {} in thread {}", recordId, Thread.currentThread().getName());
        simulateDelay(500);
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty!");
        }
        log.info("Email was sent");
    }

    @Async
    public CompletableFuture<byte[]> generatePdfReceipt(Long recordId) {
        log.info("Preparing PDF in {}", Thread.currentThread().getName());
        simulateDelay(1000);
        String pdfContent = "Receipt for Record ID: " + recordId;
        return CompletableFuture.completedFuture(pdfContent.getBytes(StandardCharsets.UTF_8));
    }

    @Async
    public CompletableFuture<String> fetchBookRating(Long bookId) {
        log.info("Fetching rating from external API for book {} in {}", bookId, Thread.currentThread().getName());
        simulateDelay(1000);
        if (bookId == 999L) {
            throw new RuntimeException("External API is unavailable");
        }
        return CompletableFuture.completedFuture("Rating was received");
    }

    private void simulateDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Async
    public void sendBookBorrowedNotification(UUID userId, String bookTitle, java.time.LocalDateTime borrowDate) {
        log.info("Sending email to the user with ID {}: You've borrowed the book '{}'. Date: {} in thread {}",
                userId, bookTitle, borrowDate, Thread.currentThread().getName());
        simulateDelay(500);
        log.info("Email was sent successfully!");
    }

    @Transactional
    public void processOverdueReminders(int daysOverdue, String messageType) {
        log.info("Processing '{}' reminders for records overdue by {} days", messageType, daysOverdue);
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(daysOverdue);
        boolean alreadyProcessed = false;
        if (alreadyProcessed) {
            log.info("Reminders for threshold {} days already sent today. Skipping.", daysOverdue);
            return;
        }
        simulateDelay(1000);
        log.info("Successfully sent {} reminders.", messageType);

    }
}