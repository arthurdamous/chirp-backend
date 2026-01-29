package com.plcoding.chirp.api.dto

import com.plcoding.chirp.domain.type.UserId
import jakarta.validation.constraints.Size

data class CreateChatRequest(
    @field:Size(min = 1, message = "Must include at least one user")
    val otherUserIds: List<UserId>
)
