package com.ascariaa.library.controller;

import com.ascariaa.library.domain.dto.BorrowRecordDto;
import com.ascariaa.library.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    @PostMapping("/{bookId}")
    public BorrowRecordDto borrowBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        return borrowRecordService.borrowBook(bookId, userId);
    }

    @PostMapping("/return/{recordId}")
    public BorrowRecordDto returnBook(
            @PathVariable Long recordId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        return borrowRecordService.returnBook(recordId, userId);
    }
}