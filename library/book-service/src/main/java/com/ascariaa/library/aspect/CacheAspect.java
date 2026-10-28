package com.ascariaa.library.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class CacheAspect {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    @Around("@annotation(com.ascariaa.library.domain.annotation.CacheResult)")
    public Object cacheResult(ProceedingJoinPoint pjp) throws Throwable {
        String key = pjp.getSignature().toShortString() + Arrays.toString(pjp.getArgs());

        if (cache.containsKey(key)) {
            log.info("Cache HIT: {}", key);
            return cache.get(key);
        }

        log.info("Cache MISS: {}. Method invocation.", key);
        Object result = pjp.proceed();
        cache.put(key, result);
        return result;
    }
}