package com.ascariaa.library.aspect;

import com.ascariaa.library.domain.annotation.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class RateLimitAspect {
    private final Map<String, Long> lastAccessMap = new ConcurrentHashMap<>();

    @Before("@annotation(rateLimit)")
    public void checkRateLimit(JoinPoint jp, RateLimit rateLimit) {
        String key = jp.getSignature().toShortString();
        long currentTime = System.currentTimeMillis();
        long lastAccess = lastAccessMap.getOrDefault(key, 0L);

        if (currentTime - lastAccess < rateLimit.minIntervalMillis()) {
            log.warn("Rate limit exceeded for {}", key);
            throw new IllegalStateException("Занадто часті запити. Зачекайте.");
        }
        lastAccessMap.put(key, currentTime);
    }
}