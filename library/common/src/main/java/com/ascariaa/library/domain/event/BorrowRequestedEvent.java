package com.ascariaa.library.domain.event;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class BorrowRequestedEvent {
    private UUID eventId = UUID.randomUUID();
    private Long recordId;
    private Long bookId;
    private UUID userId;
    private LocalDateTime timestamp;
}