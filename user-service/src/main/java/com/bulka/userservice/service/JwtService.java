package com.bulka.userservice.service;

import com.bulka.userservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtService(
            @Value("${jwt.private-key-path}") Resource privateKeyResource,
            @Value("${jwt.public-key-path}") Resource publicKeyResource) {
        this.privateKey = loadPrivateKey(privateKeyResource);
        this.publicKey = loadPublicKey(publicKeyResource);
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(Duration.ofMinutes(15));

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();

    }

    public boolean isValid(String token) {
        try{
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);

            return true;
        }
        catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PrivateKey loadPrivateKey(Resource resource) {
        try {
            String pem = new String(
                    resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String key = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);

            PKCS8EncodedKeySpec keySpec =
                    new PKCS8EncodedKeySpec(keyBytes);

            KeyFactory keyFactory =
                    KeyFactory.getInstance("RSA");

            return keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load JWT private key",
                    e
            );
        }
    }

    private PublicKey loadPublicKey(Resource resource) {
        try {
            String pem = new String(
                    resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String key = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);

            X509EncodedKeySpec keySpec =
                    new X509EncodedKeySpec(keyBytes);

            KeyFactory keyFactory =
                    KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(keySpec);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load JWT public key",
                    e
            );
        }
    }
}
