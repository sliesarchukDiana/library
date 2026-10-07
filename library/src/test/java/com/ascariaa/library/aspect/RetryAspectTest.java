package com.ascariaa.library.aspect;

import com.ascariaa.library.domain.annotation.RetryOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RetryAspectTest {

    @Test
    void retryAspect_ShouldProceedMultipleTimes_WhenExceptionIsThrown() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("unstableMethod");

        RetryOperation annotation = mock(RetryOperation.class);
        when(annotation.maxAttempts()).thenReturn(3);

        when(pjp.proceed())
                .thenThrow(new RuntimeException("Connection timeout"))
                .thenThrow(new RuntimeException("Database locked"))
                .thenReturn("Success Result");

        RetryAspect aspect = new RetryAspect();
        Object result = aspect.retry(pjp, annotation);

        assertEquals("Success Result", result);
        verify(pjp, times(3)).proceed();
    }

    @Test
    void retryAspect_ShouldThrowException_WhenMaxAttemptsReached() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("failingMethod");

        RetryOperation annotation = mock(RetryOperation.class);
        when(annotation.maxAttempts()).thenReturn(2);

        when(pjp.proceed()).thenThrow(new RuntimeException("Fatal Error"));

        RetryAspect aspect = new RetryAspect();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> aspect.retry(pjp, annotation));

        assertEquals("Fatal Error", exception.getMessage());
        verify(pjp, times(2)).proceed();
    }
}