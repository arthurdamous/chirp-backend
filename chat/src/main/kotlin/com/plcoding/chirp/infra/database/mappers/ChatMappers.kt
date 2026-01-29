package com.plcoding.chirp.infra.database.mappers

import com.plcoding.chirp.infra.database.entities.ChatEntity
import com.plcoding.chirp.infra.database.entities.ChatParticipantEntity
import com.plcoding.chirp.domain.models.Chat
import com.plcoding.chirp.domain.models.ChatMessage
import com.plcoding.chirp.domain.models.ChatParticipant
import com.plcoding.chirp.infra.database.entities.ChatMessageEntity

fun ChatEntity.toChat(lastMessage: ChatMessage? = null) = Chat(
    id = id!!,
    participants = participants.map { it.toChatParticipant() }.toSet(),
    creator = creator.toChatParticipant(),
    lastActivityAt = lastMessage?.createdAt ?: createdAt,
    lastMessage = lastMessage,
    createdAt = createdAt
)

fun ChatParticipantEntity.toChatParticipant(): ChatParticipant = ChatParticipant(
    userId = userId,
    username = username,
    email = email,
    profilePictureUrl = profilePictureUrl
)

fun ChatParticipant.toChatParticipantEntity() = ChatParticipantEntity(
    userId = userId,
    username = username,
    email = email,
    profilePictureUrl = profilePictureUrl
)

fun ChatMessageEntity.toChatMessage(): ChatMessage {
    return ChatMessage(
        id = id!!,
        chatId = chatId,
        sender = sender.toChatParticipant(),
        content = content,
        createdAt = createdAt
    )
}