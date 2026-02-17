package com.plcoding.chirp.infra.mappers

import com.plcoding.chirp.domain.model.DeviceToken
import com.plcoding.chirp.infra.database.PlatformEntity

fun DeviceToken.Platform.toPlatformEntity(): PlatformEntity {
    return when (this) {
        DeviceToken.Platform.IOS -> PlatformEntity.IOS
        DeviceToken.Platform.ANDROID -> PlatformEntity.ANDROID
    }
}

fun PlatformEntity.toPlatform(): DeviceToken.Platform {
    return when (this) {
        PlatformEntity.IOS -> DeviceToken.Platform.IOS
        PlatformEntity.ANDROID -> DeviceToken.Platform.ANDROID
    }
}