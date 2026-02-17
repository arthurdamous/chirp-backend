package com.plcoding.chirp.api.dto

import com.plcoding.chirp.domain.type.UserId
import java.time.Instant

data class DeviceTokenDto(
    val token: String,
    val userId: UserId,
    val createdAt: Instant
)
