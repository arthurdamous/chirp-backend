package com.plcoding.chirp.api.dto

import com.plcoding.chirp.domain.type.UserId

data class ChatParticipantDto(
    val id: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String?
)
