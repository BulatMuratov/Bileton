package com.bulka.eventservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "idempotency_keys",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_idempotency_keys_key_operation",
                        columnNames = {"key", "operation"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyKey {

    @Id
    private UUID id;

    @Column(name = "key", nullable = false, length = 100)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IdempotencyOperation operation;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}