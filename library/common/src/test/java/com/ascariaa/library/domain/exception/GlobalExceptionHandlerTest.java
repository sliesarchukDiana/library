package com.ascariaa.library.domain.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEntityNotFound() {
        ErrorResponse response = handler.handleEntityNotFound(new EntityNotFoundException("Not found"));

        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
        assertEquals("Not found", response.message());
    }

    @Test
    void handleIllegalState() {
        ErrorResponse response = handler.handleIllegalState(new IllegalStateException("Bad state"));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad state", response.message());
    }

    @Test
    void handleValidationExceptions() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("object", "field", "Error message")));

        ErrorResponse response = handler.handleValidationExceptions(ex);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("field: Error message", response.message());
    }

    @Test
    void handleGenericException() {
        ErrorResponse response = handler.handleGenericException(new Exception("Some error"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
        assertEquals("Internal server error occurred.", response.message());
    }

    @Test
    void handleTaskRejected() {
        ErrorResponse response = handler.handleTaskRejected(new TaskRejectedException("Rejected"));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.status());
        assertEquals("Too many requests. Try again later.", response.message());
    }

    @Test
    void handleTimeout() {
        ErrorResponse response = handler.handleTimeout(new TimeoutException("Timeout"));

        assertEquals(HttpStatus.REQUEST_TIMEOUT.value(), response.status());
        assertEquals("Time of waiting has run out.", response.message());
    }
}