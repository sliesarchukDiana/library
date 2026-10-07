package com.ascariaa.library.aspect;

import com.ascariaa.library.domain.annotation.RateLimit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RateLimitAspectTest {

    @Test
    void rateLimitAspect_ShouldThrowException_WhenCallsAreTooFrequent() {
        JoinPoint jp = mock(JoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(jp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("BookService.createBook");

        RateLimit annotation = mock(RateLimit.class);
        when(annotation.minIntervalMillis()).thenReturn(5000L);

        RateLimitAspect aspect = new RateLimitAspect();

        assertDoesNotThrow(() -> aspect.checkRateLimit(jp, annotation));

        assertThrows(IllegalStateException.class, () -> aspect.checkRateLimit(jp, annotation));
    }
}