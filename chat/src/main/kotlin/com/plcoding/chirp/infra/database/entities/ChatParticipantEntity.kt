package com.plcoding.chirp.infra.database.entities

import com.plcoding.chirp.domain.type.UserId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(
    name = "chat_participants",
    schema = "chat_service",
    indexes = [
        Index(name = "idx_chat_participants_email", columnList = "email"),
        Index(name = "idx_chat_participants_username", columnList = "username"),
    ]
)
class ChatParticipantEntity(
    @Id
    var userId: UserId,
    @Column(nullable = false, unique = true)
    var username: String,
    @Column(nullable = false, unique = true)
    var email: String,
    @Column(nullable = true)
    var profilePictureUrl: String? = null,
    @CreationTimestamp
    var createdAt: Instant = Instant.now()
)