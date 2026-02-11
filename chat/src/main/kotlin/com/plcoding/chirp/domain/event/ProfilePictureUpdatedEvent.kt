package com.plcoding.chirp.domain.event

import com.plcoding.chirp.domain.type.UserId

data class ProfilePictureUpdatedEvent(
    val userId: UserId,
    val newUrl: String?
)