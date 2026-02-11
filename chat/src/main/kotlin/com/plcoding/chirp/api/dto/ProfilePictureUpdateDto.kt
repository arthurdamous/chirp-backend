package com.plcoding.chirp.api.dto

import com.plcoding.chirp.domain.type.UserId

data class ProfilePictureUpdateDto(
    val userId: UserId,
    val newUrl: String?
)
