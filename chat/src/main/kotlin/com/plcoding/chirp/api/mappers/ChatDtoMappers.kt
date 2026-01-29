package com.plcoding.chirp.api.mappers

import com.plcoding.chirp.api.dto.ChatDto
import com.plcoding.chirp.api.dto.ChatMessageDto
import com.plcoding.chirp.api.dto.ChatParticipantDto
import com.plcoding.chirp.domain.models.Chat
import com.plcoding.chirp.domain.models.ChatMessage
import com.plcoding.chirp.domain.models.ChatParticipant


fun Chat.toChatDto() = ChatDto(
    id = id,
    participants = participants.map { it.toChatParticipantDto() },
    lastActivityAt = lastActivityAt,
    lastMessage = lastMessage?.toChatMessageDto(),
    creator = creator.toChatParticipantDto()
)

fun ChatMessage.toChatMessageDto() = ChatMessageDto(
    id = id,
    chatId = chatId,
    content = content,
    createdAt = createdAt,
    senderId = sender.userId
)

fun ChatParticipant.toChatParticipantDto() = ChatParticipantDto(
    id = userId,
    username = username,
    email = email,
    profilePictureUrl = profilePictureUrl
)