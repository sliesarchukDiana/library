package com.ascariaa.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class AsyncLibraryServiceTest {

    private AsyncLibraryService asyncLibraryService;

    @BeforeEach
    void setUp() {
        asyncLibraryService = new AsyncLibraryService();
    }

    @Test
    void sendNotification_success() {
        assertDoesNotThrow(() -> asyncLibraryService.sendNotification(1L, "test@test.com"));
    }

    @Test
    void sendNotification_emptyEmail_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> asyncLibraryService.sendNotification(1L, ""));
    }

    @Test
    void generatePdfReceipt_returnsFutureWithBytes() throws ExecutionException, InterruptedException {
        CompletableFuture<byte[]> future = asyncLibraryService.generatePdfReceipt(1L);
        assertNotNull(future.get());
        assertTrue(future.get().length > 0);
    }

    @Test
    void fetchBookRating_success() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = asyncLibraryService.fetchBookRating(1L);
        assertEquals("Rating was received", future.get());
    }

    @Test
    void fetchBookRating_unavailable_throwsException() {
        assertThrows(RuntimeException.class, () -> asyncLibraryService.fetchBookRating(999L));
    }

    @Test
    void sendBookBorrowedNotification_success() {
        assertDoesNotThrow(() -> asyncLibraryService.sendBookBorrowedNotification(UUID.randomUUID(), "Title", LocalDateTime.now()));
    }
}