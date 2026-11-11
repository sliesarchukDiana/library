package com.ascariaa.library.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheAspectTest {

    @InjectMocks
    private CacheAspect cacheAspect;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private Signature signature;

    @BeforeEach
    void setUp() {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("BookService.getBookById");
        when(pjp.getArgs()).thenReturn(new Object[]{1L});
    }

    @Test
    void cacheResult_cacheMissThenHit() throws Throwable {
        String expectedResult = "BookDto";
        when(pjp.proceed()).thenReturn(expectedResult);

        Object firstResult = cacheAspect.cacheResult(pjp);
        assertEquals(expectedResult, firstResult);
        verify(pjp, times(1)).proceed();

        Object secondResult = cacheAspect.cacheResult(pjp);
        assertEquals(expectedResult, secondResult);
        verify(pjp, times(1)).proceed();
    }
}