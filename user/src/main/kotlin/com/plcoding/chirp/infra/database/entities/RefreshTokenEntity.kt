package com.plcoding.chirp.infra.database.entities

import com.plcoding.chirp.domain.model.UserId
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(
    name = "refresh_tokens",
    schema = "user_service",
    indexes = [
        Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
        Index(name = "idx_refresh_tokens_user_token", columnList = "user_id, hashed_token")
    ]
)
class RefreshTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(nullable = false)
    var userId: UserId,
    @Column(nullable = false)
    var hashedToken: String,
    @Column(nullable = false)
    val expiresAt: Instant = Instant.now(),
    @CreationTimestamp
    var createdAt: Instant = Instant.now()
)