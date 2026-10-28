package com.ascariaa.library.config.resolver;

import com.ascariaa.library.domain.annotation.CurrentUserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserIdArgumentResolverTest {
    private CurrentUserIdArgumentResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new CurrentUserIdArgumentResolver();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void supportsParameter_withAnnotation_returnsTrue() throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(
                TestController.class.getMethod("methodWithAnnotation", UUID.class), 0);
        assertTrue(resolver.supportsParameter(parameter));
    }

    @SuppressWarnings("unused")
    private static class TestController {
        public void methodWithAnnotation(@CurrentUserId UUID id) {}
    }
}
