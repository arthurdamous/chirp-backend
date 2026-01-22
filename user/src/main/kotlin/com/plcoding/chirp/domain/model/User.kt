package com.plcoding.chirp.domain.model

import com.plcoding.chirp.domain.type.UserId


data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val hasEmailVerified: Boolean
)
