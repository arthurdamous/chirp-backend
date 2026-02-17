package com.plcoding.chirp.infra.mappers

import com.plcoding.chirp.domain.model.DeviceToken
import com.plcoding.chirp.infra.database.DeviceTokenEntity

fun DeviceTokenEntity.toDeviceToken() = DeviceToken(
    userId = userId,
    token = token,
    platform = platform.toPlatform(),
    createdAt = createdAt,
    id = id
)