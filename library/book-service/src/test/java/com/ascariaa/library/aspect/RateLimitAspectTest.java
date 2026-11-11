package com.ascariaa.library.aspect;

import com.ascariaa.library.domain.annotation.RateLimit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitAspectTest {

    @InjectMocks
    private RateLimitAspect rateLimitAspect;

    @Mock
    private JoinPoint jp;

    @Mock
    private Signature signature;

    @Mock
    private RateLimit rateLimit;

    @BeforeEach
    void setUp() {
        when(jp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("BookService.createBook");
        when(rateLimit.minIntervalMillis()).thenReturn(5000L);
    }

    @Test
    void checkRateLimit_successThenThrowsException() {
        assertDoesNotThrow(() -> rateLimitAspect.checkRateLimit(jp, rateLimit));

        assertThrows(IllegalStateException.class, () -> rateLimitAspect.checkRateLimit(jp, rateLimit));
    }
}