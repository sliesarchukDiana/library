package com.ascariaa.library.aspect;

import com.ascariaa.library.domain.annotation.RetryOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RetryAspect {

    @Around("@annotation(retryOperation)")
    public Object retry(ProceedingJoinPoint pjp, RetryOperation retryOperation) throws Throwable {
        int attempts = 0;
        Throwable lastException = null;

        while (attempts < retryOperation.maxAttempts()) {
            try {
                return pjp.proceed();
            } catch (Exception e) {
                attempts++;
                log.warn("Something went wrong {}. attempt {}/{}", pjp.getSignature().getName(), attempts, retryOperation.maxAttempts());
                lastException = e;
            }
        }
        assert lastException != null;
        throw lastException;
    }
}