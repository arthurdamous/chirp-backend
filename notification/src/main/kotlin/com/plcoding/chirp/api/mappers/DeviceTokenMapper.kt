package com.plcoding.chirp.api.mappers

import com.plcoding.chirp.api.dto.DeviceTokenDto
import com.plcoding.chirp.api.dto.PlatformDto
import com.plcoding.chirp.domain.model.DeviceToken

fun DeviceToken.toDeviceTokenDto() = DeviceTokenDto(
    token = token,
    userId = userId,
    createdAt = createdAt
)

fun PlatformDto.toPlatform(): DeviceToken.Platform {
    return when (this) {
        PlatformDto.IOS -> DeviceToken.Platform.IOS
        PlatformDto.ANDROID -> DeviceToken.Platform.ANDROID
    }
}