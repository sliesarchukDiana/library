package com.ascariaa.library.config.resolver;

import com.ascariaa.library.config.resolver.CurrentUserIdArgumentResolver;
import com.ascariaa.library.domain.annotation.CurrentUserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void supportsParameter_withoutAnnotation_returnsFalse() throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(
                TestController.class.getMethod("methodWithoutAnnotation", UUID.class), 0);
        assertFalse(resolver.supportsParameter(parameter));
    }

    @Test
    void resolveArgument_validAuthentication_returnsUuid() throws Exception {
        UUID expectedUuid = UUID.randomUUID();

        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(expectedUuid.toString());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        MethodParameter parameter = new MethodParameter(
                TestController.class.getMethod("methodWithAnnotation", UUID.class), 0);

        Object result = resolver.resolveArgument(parameter, null, null, null);

        assertEquals(expectedUuid, result);
    }

    @SuppressWarnings("unused")
    private static class TestController {
        public void methodWithAnnotation(@CurrentUserId UUID id) {}
        public void methodWithoutAnnotation(UUID id) {}
    }
}