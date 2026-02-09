package com.plcoding.chirp.api.dto.ws

import com.plcoding.chirp.domain.type.ChatId
import com.plcoding.chirp.domain.type.ChatMessageId

data class DeletedMessageDto(
    val chatId: ChatId,
    val messageId: ChatMessageId
)
