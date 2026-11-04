package com.ascariaa.library.service.consumer;

import com.ascariaa.library.domain.entity.Book;
import com.ascariaa.library.domain.event.BorrowConfirmedEvent;
import com.ascariaa.library.domain.event.BorrowRejectedEvent;
import com.ascariaa.library.domain.event.BorrowRequestedEvent;
import com.ascariaa.library.repository.BookRepository;
import com.ascariaa.library.repository.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowRequestConsumerTest {

    @Mock private BookRepository bookRepository;
    @Mock private ProcessedEventRepository processedEventRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private BorrowRequestConsumer consumer;

    @Test
    void consumeBorrowRequest_alreadyProcessed_doesNothing() {
        BorrowRequestedEvent event = new BorrowRequestedEvent(UUID.randomUUID(), 1L, 1L, UUID.randomUUID(), LocalDateTime.now());
        when(processedEventRepository.existsById(event.getEventId())).thenReturn(true);

        consumer.consumeBorrowRequest(event);

        verify(bookRepository, never()).findById(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void consumeBorrowRequest_bookAvailable_publishesConfirmed() {
        BorrowRequestedEvent event = new BorrowRequestedEvent(UUID.randomUUID(), 1L, 1L, UUID.randomUUID(), LocalDateTime.now());
        Book book = Book.builder().id(1L).title("Test").availableCopies(1).build();

        when(processedEventRepository.existsById(event.getEventId())).thenReturn(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        consumer.consumeBorrowRequest(event);

        verify(bookRepository).save(book);
        verify(eventPublisher).publishEvent(any(BorrowConfirmedEvent.class));
        verify(processedEventRepository).save(any());
    }

    @Test
    void consumeBorrowRequest_noAvailableCopies_publishesRejected() {
        BorrowRequestedEvent event = new BorrowRequestedEvent(UUID.randomUUID(), 1L, 1L, UUID.randomUUID(), LocalDateTime.now());
        Book book = Book.builder().id(1L).title("Test").availableCopies(0).build();

        when(processedEventRepository.existsById(event.getEventId())).thenReturn(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        consumer.consumeBorrowRequest(event);

        verify(bookRepository, never()).save(book);
        verify(eventPublisher).publishEvent(any(BorrowRejectedEvent.class));
        verify(processedEventRepository).save(any());
    }

    @Test
    void consumeBorrowRequest_bookNotFound_publishesRejected() {
        BorrowRequestedEvent event = new BorrowRequestedEvent(UUID.randomUUID(), 1L, 1L, UUID.randomUUID(), LocalDateTime.now());

        when(processedEventRepository.existsById(event.getEventId())).thenReturn(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        consumer.consumeBorrowRequest(event);

        verify(eventPublisher).publishEvent(any(BorrowRejectedEvent.class));
        verify(processedEventRepository).save(any());
    }
}