package com.ascariaa.library.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CacheAspectTest {

    @Test
    void cacheAspect_ShouldSkipProceed_OnSubsequentCalls() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("BookService.getBookById");
        when(pjp.getArgs()).thenReturn(new Object[]{1L});

        when(pjp.proceed()).thenReturn("Cached_Data");

        CacheAspect aspect = new CacheAspect();

        Object result1 = aspect.cacheResult(pjp);
        Object result2 = aspect.cacheResult(pjp);
        Object result3 = aspect.cacheResult(pjp);

        assertEquals("Cached_Data", result1);
        assertEquals("Cached_Data", result2);
        assertEquals("Cached_Data", result3);

        verify(pjp, times(1)).proceed();
    }
}