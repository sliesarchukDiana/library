package com.ascariaa.library.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    @Test
    void userAuthoritiesMapper_extractsRoles() {
        GrantedAuthoritiesMapper mapper = config.userAuthoritiesMapper();

        Map<String, Object> claims = Map.of("roles", List.of("ADMIN", "USER"));
        OidcIdToken token = new OidcIdToken("token", Instant.now(), Instant.now().plusSeconds(60), claims);
        OidcUserAuthority authority = new OidcUserAuthority(token);

        Collection<? extends GrantedAuthority> authorities = mapper.mapAuthorities(Set.of(authority));

        assertTrue(authorities.stream().anyMatch(a -> Objects.equals(a.getAuthority(), "ADMIN")));
        assertTrue(authorities.stream().anyMatch(a -> Objects.equals(a.getAuthority(), "USER")));
    }
}