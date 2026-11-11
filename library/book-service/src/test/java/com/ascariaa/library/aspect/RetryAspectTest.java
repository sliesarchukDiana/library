package com.ascariaa.library.aspect;

import com.ascariaa.library.domain.annotation.RetryOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetryAspectTest {

    @InjectMocks
    private RetryAspect retryAspect;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private RetryOperation retryOperation;

    @Mock
    private Signature signature;

    @Test
    void retry_successOnFirstAttempt() throws Throwable {
        when(retryOperation.maxAttempts()).thenReturn(3);
        when(pjp.proceed()).thenReturn("Success");

        Object result = retryAspect.retry(pjp, retryOperation);

        assertEquals("Success", result);
        verify(pjp, times(1)).proceed();
    }

    @Test
    void retry_successOnSecondAttempt() throws Throwable {
        when(retryOperation.maxAttempts()).thenReturn(3);
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("updateBook");

        when(pjp.proceed())
                .thenThrow(new RuntimeException("Temporary failure"))
                .thenReturn("Success");

        Object result = retryAspect.retry(pjp, retryOperation);

        assertEquals("Success", result);
        verify(pjp, times(2)).proceed();
    }

    @Test
    void retry_exceedsMaxAttempts() throws Throwable {
        when(retryOperation.maxAttempts()).thenReturn(2);
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("updateBook");

        when(pjp.proceed())
                .thenThrow(new RuntimeException("Failure 1"))
                .thenThrow(new IllegalArgumentException("Failure 2"));

        assertThrows(IllegalArgumentException.class, () -> retryAspect.retry(pjp, retryOperation));
        verify(pjp, times(2)).proceed();
    }
}