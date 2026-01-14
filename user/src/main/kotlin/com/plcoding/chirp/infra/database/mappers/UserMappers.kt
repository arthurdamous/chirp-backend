package com.plcoding.chirp.infra.database.mappers

import com.plcoding.chirp.domain.model.User
import com.plcoding.chirp.infra.database.entities.UserEntity

fun UserEntity.toUser() = User(
    id = id!!,
    username = username,
    email = email,
    hasEmailVerified = hasVerifiedEmail
)