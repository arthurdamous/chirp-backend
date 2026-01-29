package com.plcoding.chirp.domain.exception

import com.plcoding.chirp.domain.type.UserId

class ChatParticipantNotFoundException(
    val userId: UserId
): RuntimeException("Chat participant not found for user with id $userId") {
}