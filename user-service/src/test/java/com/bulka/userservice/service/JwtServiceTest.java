package com.bulka.userservice.service;

import com.bulka.userservice.model.Role;
import com.bulka.userservice.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        Resource privateKey =
                new ClassPathResource(
                        "keys/private_key.pem"
        );
        Resource publicKey =
                new ClassPathResource(
                        "keys/public_key.pem"
        );
        jwtService =
                new JwtService(
                        privateKey,
                        publicKey
        );
    }

    @Test
    void generateAccessToken_shouldGenerateToken() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .role(Role.USER)
                .build();

        String token =
                jwtService.generateAccessToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateAccessToken_shouldContainUserId() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .role(Role.USER)
                .build();

        String token = jwtService.generateAccessToken(user);
        Claims claims = jwtService.getClaims(token);

        assertEquals(userId.toString(), claims.getSubject());
        assertEquals("USER", claims.get("role", String.class));
    }

    @Test
    void isValid_shouldReturnTrue_forValidToken() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .role(Role.USER)
                .build();

        String token =
                jwtService.generateAccessToken(user);

        assertTrue(
                jwtService.isValid(token)
        );
    }

    @Test
    void isValid_shouldReturnFalse_forInvalidToken() {

        String token = "this-is-not-a-jwt";

        assertFalse(jwtService.isValid(token));
    }

    @Test
    void isValid_shouldReturnFalse_forNullToken() {
        assertFalse(jwtService.isValid(null));
    }

    @Test
    void getClaims_shouldReturnTokenClaims() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .role(Role.ADMIN)
                .build();

        String token = jwtService.generateAccessToken(user);

        Claims claims = jwtService.getClaims(token);

        assertEquals(userId.toString(), claims.getSubject());

        assertEquals("ADMIN", claims.get("role", String.class));
    }



}