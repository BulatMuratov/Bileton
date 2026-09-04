package com.bulka.userservice.service;


import com.bulka.userservice.exception.auth.InvalidRefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);
    private static final String REFRESH_TOKEN_PREFIX = "refresh-token:";

    private static final DefaultRedisScript<String> CONSUME_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    local userId = redis.call('GET', KEYS[1])
    
                    if not userId then
                        return nil
                    end
    
                    redis.call('DEL', KEYS[1])
    
                    return userId
                    """,
                    String.class
            );

    public String create(UUID userId){
        String refreshToken = generateToken();

        String key = REFRESH_TOKEN_PREFIX + hash(refreshToken);

        redisTemplate
                .opsForValue()
                .set(key, userId.toString(), REFRESH_TOKEN_TTL);
        return refreshToken;
    }

    public UUID consume(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + hash(refreshToken);

        String userId = redisTemplate.execute(CONSUME_SCRIPT, List.of(key));

        if(userId == null) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        return UUID.fromString(userId);
    }

    public UUID validate(String refreshToken){
        String key =  REFRESH_TOKEN_PREFIX + hash(refreshToken);

        String userId = redisTemplate
                .opsForValue()
                .get(key);

        if(userId == null){
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        return UUID.fromString(userId);
    }

    public void revoke(String refreshToken){
        String key = REFRESH_TOKEN_PREFIX + hash(refreshToken);

        redisTemplate.delete(key);
    }

    private String generateToken() {
        byte[] randomBytes = new  byte[32];

        new java.security.SecureRandom().nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private String hash(String token) {
        try {
            byte[] bytes = token.getBytes(StandardCharsets.UTF_8);
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(bytes);

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable",
                    ex
            );
        }
    }
}
