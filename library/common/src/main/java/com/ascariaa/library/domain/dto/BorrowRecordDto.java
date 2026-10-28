package com.ascariaa.library.domain.dto;

import com.ascariaa.library.domain.enums.BorrowStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowRecordDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private BorrowStatus status;
}