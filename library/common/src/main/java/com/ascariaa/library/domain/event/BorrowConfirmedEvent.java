package com.ascariaa.library.domain.event;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class BorrowConfirmedEvent {
    private UUID eventId = UUID.randomUUID();
    private Long recordId;
    private String bookTitle;
    private UUID userId;
    private LocalDateTime timestamp;
}