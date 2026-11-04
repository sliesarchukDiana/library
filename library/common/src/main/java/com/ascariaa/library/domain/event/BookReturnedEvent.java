package com.ascariaa.library.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookReturnedEvent {
    private UUID eventId = UUID.randomUUID();
    private Long bookId;
    private LocalDateTime timestamp;
}
