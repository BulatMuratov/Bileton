package com.bulka.eventservice.repository;

import com.bulka.eventservice.model.IdempotencyKey;
import com.bulka.eventservice.model.IdempotencyOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyKeyRepository
        extends JpaRepository<IdempotencyKey, UUID> {

    Optional<IdempotencyKey> findByKeyAndOperation(
            String key,
            IdempotencyOperation operation
    );

    @Modifying
    @Query(value = """
        INSERT INTO idempotency_keys (
            id, key, operation, resource_id, created_at
        ) VALUES (:id, :key, :operation, :resourceId, CURRENT_TIMESTAMP
        )
        ON CONFLICT (key, operation) DO NOTHING
        """, nativeQuery = true)
    int insertIfAbsent(
            @Param("id") UUID id,
            @Param("key") String key,
            @Param("operation") String operation,
            @Param("resourceId") UUID resourceId
    );
}