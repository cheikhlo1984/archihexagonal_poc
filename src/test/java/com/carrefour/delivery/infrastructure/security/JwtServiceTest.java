package com.carrefour.delivery.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // 256-bit key (Base64-encoded)
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86400000L);
    }

    @Test
    void generateToken_andExtractUsername() {
        String token = jwtService.generateToken("alice");
        String extracted = jwtService.extractUsername(token);
        assertThat(extracted).isEqualTo("alice");
    }

    @Test
    void isTokenValid_returnsTrueForValidToken() {
        String token = jwtService.generateToken("bob");
        assertThat(jwtService.isTokenValid(token, "bob")).isTrue();
    }

    @Test
    void isTokenValid_returnsFalseForWrongUsername() {
        String token = jwtService.generateToken("alice");
        assertThat(jwtService.isTokenValid(token, "bob")).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForExpiredToken() {
        // Create service with very short expiry
        JwtService shortLivedService = new JwtService();
        ReflectionTestUtils.setField(shortLivedService, "secretKey",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(shortLivedService, "expirationMs", 1L); // 1 ms

        String token = shortLivedService.generateToken("alice");

        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        assertThat(shortLivedService.isTokenValid(token, "alice")).isFalse();
    }
}
