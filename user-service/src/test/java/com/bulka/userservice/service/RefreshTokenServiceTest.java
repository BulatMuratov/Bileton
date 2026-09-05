package com.bulka.userservice.service;

import com.bulka.userservice.exception.auth.InvalidRefreshTokenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    public void create_shouldCreateRefreshTokenSuccessfully(){
        UUID userId = UUID.randomUUID();

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        String refreshToken =
                refreshTokenService.create(userId);

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isBlank());

        verify(valueOperations).set(
                anyString(),
                Mockito.eq(userId.toString()),
                Mockito.eq(Duration.ofDays(30))
        );
    }

    @Test
    public void consume_shouldReturnUserId_whenTokenIsValid(){
        UUID userId = UUID.randomUUID();
        String refreshToken = "refresh-token";

        when(redisTemplate.execute(
                Mockito.any(),
                anyList()
        )).thenReturn(userId.toString());

        UUID result =
                refreshTokenService.consume(refreshToken);

        assertEquals(userId, result);

        verify(redisTemplate).execute(
                Mockito.any(),
                anyList()
        );
    }

    @Test
    public void consume_shouldThrowException_whenTokenDoesNotExist() {
        String refreshToken = "invalid-token";

        when(redisTemplate.execute(
                Mockito.any(),
                anyList()
        )).thenReturn(null);

        InvalidRefreshTokenException exception =
                assertThrows(
                        InvalidRefreshTokenException.class,
                        () -> refreshTokenService.consume(refreshToken)
                );

        assertEquals(
                "Invalid refresh token",
                exception.getMessage()
        );
    }

    @Test
    void revoke_shouldDeleteRefreshToken() {

        String refreshToken = "refresh-token";

        refreshTokenService.revoke(refreshToken);

        verify(redisTemplate)
                .delete(anyString());
    }
}