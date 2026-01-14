package com.plcoding.chirp.api.dto

import com.plcoding.chirp.domain.model.UserId

data class UserDto(
    val id: UserId,
    val email: String,
    val username: String,
    val hasVerifiedEmail: Boolean
)
