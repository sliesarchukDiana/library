package com.ascariaa.library.service.consumer;

import com.ascariaa.library.domain.event.BookReturnedEvent;
import com.ascariaa.library.repository.ProcessedEventRepository;
import com.ascariaa.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookEventConsumerTest {

    @Mock private BookService bookService;
    @Mock private ProcessedEventRepository processedEventRepository;

    @InjectMocks private BookEventConsumer consumer;

    @Test
    void consumeBookReturned_alreadyProcessed_skips() {
        BookReturnedEvent event = new BookReturnedEvent(UUID.randomUUID(), 1L, LocalDateTime.now());
        when(processedEventRepository.existsById(event.getEventId())).thenReturn(true);

        consumer.consumeBookReturned(event);

        verify(bookService, never()).incrementCopies(any());
    }

    @Test
    void consumeBookReturned_notProcessed_incrementsAndSaves() {
        BookReturnedEvent event = new BookReturnedEvent(UUID.randomUUID(), 1L, LocalDateTime.now());
        when(processedEventRepository.existsById(event.getEventId())).thenReturn(false);

        consumer.consumeBookReturned(event);

        verify(bookService).incrementCopies(1L);
        verify(processedEventRepository).save(any());
    }
}