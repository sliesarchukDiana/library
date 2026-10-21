package com.ascariaa.library.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AsyncLibraryService {

    @Async
    public void sendBorrowNotification(Long recordId, String email) {
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
}