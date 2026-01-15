package com.plcoding.chirp.infra.database.mappers

import com.plcoding.chirp.domain.model.EmailVerificationToken
import com.plcoding.chirp.infra.database.entities.EmailVerificationTokenEntity

fun EmailVerificationTokenEntity.toEmailVerificationToken(): EmailVerificationToken {
    return EmailVerificationToken(
        id = id,
        token = token,
        user = user.toUser()
    )
}