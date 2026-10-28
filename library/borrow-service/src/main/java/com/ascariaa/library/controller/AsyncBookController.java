package com.ascariaa.library.controller;

import com.ascariaa.library.domain.dto.BookCardDto;
import com.ascariaa.library.service.AsyncLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/books/async")
@RequiredArgsConstructor
public class AsyncBookController {

    private final AsyncLibraryService asyncLibraryService;

    @GetMapping("/{bookId}/card/{recordId}")
    public CompletableFuture<ResponseEntity<BookCardDto>> getBookCard(
            @PathVariable Long bookId,
            @PathVariable Long recordId) {

        CompletableFuture<byte[]> pdfFuture = asyncLibraryService.generatePdfReceipt(recordId);

        CompletableFuture<String> ratingFuture = asyncLibraryService.fetchBookRating(bookId)
                .exceptionally(ex -> "Rating is currently unavailable.");

        return pdfFuture.thenCombine(ratingFuture, (pdf, rating) ->
                ResponseEntity.ok(new BookCardDto(pdf, rating))
        ).orTimeout(2, TimeUnit.SECONDS);
    }

    @PostMapping("/notify/{recordId}")
    public ResponseEntity<Void> triggerNotification(@PathVariable Long recordId, @RequestParam(required = false) String email) {
        asyncLibraryService.sendNotification(recordId, email);
        return ResponseEntity.accepted().build();
    }
}