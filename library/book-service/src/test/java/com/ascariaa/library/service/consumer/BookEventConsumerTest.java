package com.ascariaa.library.service.consumer;

import com.ascariaa.library.domain.event.BookBorrowedEvent;
import com.ascariaa.library.repository.ProcessedEventRepository;
import com.ascariaa.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookEventConsumerTest {

    @Mock
    private BookService bookService;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private BookEventConsumer consumer;

    @Test
    void consumeBookBorrowed_twice_processesOnlyOnce() {
        UUID eventId = UUID.randomUUID();
        Long bookId = 100L;
        BookBorrowedEvent event = new BookBorrowedEvent(eventId, bookId, LocalDateTime.now());

        when(processedEventRepository.existsById(eventId)).thenReturn(false);
        consumer.consumeBookBorrowed(event);

        verify(bookService, times(1)).decrementCopies(bookId);
        verify(processedEventRepository, times(1)).save(any());

        when(processedEventRepository.existsById(eventId)).thenReturn(true);
        consumer.consumeBookBorrowed(event);

        verify(bookService, times(1)).decrementCopies(bookId);
        verify(processedEventRepository, times(1)).save(any());
    }
}