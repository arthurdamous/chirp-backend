package com.plcoding.chirp.infra.database.repositories

import com.plcoding.chirp.infra.database.entities.ChatMessageEntity
import com.plcoding.chirp.domain.type.ChatId
import com.plcoding.chirp.domain.type.ChatMessageId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface ChatMessageRepository : JpaRepository<ChatMessageEntity, ChatMessageId> {

    @Query(
        """
            SELECT m
            FROM ChatMessageEntity m
            WHERE m.chat.id = :chatId
            AND m.createdAt < :before
            ORDER BY m.createdAt DESC
            """
    )
    fun findByChatIdBefore(
        chatId: ChatId,
        before: Instant,
        pageable: Pageable
    ): Slice<ChatMessageEntity>

    @Query(
        """
            SELECT m
            FROM ChatMessageEntity m
            LEFT JOIN FETCH m.sender
            WHERE m.chat.id IN :chatIds
            AND (m.createdAt, m.id) = (
                SELECT m2.createdAt, m2.id
                FROM ChatMessageEntity m2
                WHERE m2.chat.id = m.chatId
                ORDER BY m2.createdAt DESC LIMIT 1
            )
            """
    )
    fun findLatestMessagesByChatIds(chatIds: Set<ChatId>): List<ChatMessageEntity>
}