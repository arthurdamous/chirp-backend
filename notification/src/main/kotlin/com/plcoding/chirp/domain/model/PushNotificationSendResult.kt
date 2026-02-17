package com.plcoding.chirp.domain.model

data class PushNotificationSendResult(
    val succeeded: List<DeviceToken>,
    val temporaryFailure: List<DeviceToken>,
    val permanentFailure: List<DeviceToken>
)
