package com.bulka.paymentservice.repository;

import com.bulka.paymentservice.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyKeyRepository
        extends JpaRepository<IdempotencyKey, UUID> {

    Optional<IdempotencyKey> findByIdempotencyKeyAndUserId(
            String key,
            UUID userId
    );

    @Modifying
    @Query(value = """
        INSERT INTO idempotency_keys (
            id, user_id, idempotency_key, payment_id, created_at
        ) VALUES (:id, :userId, :key, :paymentId,CURRENT_TIMESTAMP
        )
        ON CONFLICT (user_id, idempotency_key) DO NOTHING
        """, nativeQuery = true)
    int insertIfAbsent(
            @Param("id") UUID id,
            @Param("userId") UUID userId,
            @Param("key") String key,
            @Param("paymentId") UUID paymentId
    );
}
