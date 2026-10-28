package com.ascariaa.library.controller;

import com.ascariaa.library.domain.dto.BookCardDto;
import com.ascariaa.library.service.AsyncLibraryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncBookControllerTest {

    @Mock private AsyncLibraryService asyncLibraryService;
    @InjectMocks private AsyncBookController asyncBookController;

    @Test
    void getBookCard_returnsCombinedData() throws ExecutionException, InterruptedException {
        byte[] pdfBytes = new byte[]{1, 2, 3};
        when(asyncLibraryService.generatePdfReceipt(1L)).thenReturn(CompletableFuture.completedFuture(pdfBytes));
        when(asyncLibraryService.fetchBookRating(1L)).thenReturn(CompletableFuture.completedFuture("5 Stars"));

        CompletableFuture<ResponseEntity<BookCardDto>> future = asyncBookController.getBookCard(1L, 1L);
        ResponseEntity<BookCardDto> response = future.get();

        assertNotNull(response.getBody());
        assertEquals("5 Stars", response.getBody().getRating());
        assertEquals(pdfBytes, response.getBody().getReceiptPdf());
    }

    @Test
    void triggerNotification_callsService() {
        ResponseEntity<Void> response = asyncBookController.triggerNotification(1L, "test@test.com");
        assertEquals(202, response.getStatusCode().value());
        verify(asyncLibraryService).sendNotification(1L, "test@test.com");
    }
}