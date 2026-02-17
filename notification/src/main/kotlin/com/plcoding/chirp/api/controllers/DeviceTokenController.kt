package com.plcoding.chirp.api.controllers

import com.plcoding.chirp.api.dto.DeviceTokenDto
import com.plcoding.chirp.api.dto.RegisterDeviceRequest
import com.plcoding.chirp.api.mappers.toDeviceTokenDto
import com.plcoding.chirp.api.mappers.toPlatform
import com.plcoding.chirp.api.util.requestUserId
import com.plcoding.chirp.service.PushNotificationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notification")
class DeviceTokenController(
    private val pushNotificationService: PushNotificationService
) {

    @PostMapping("/register")
    fun registerDeviceToken(
        @Valid @RequestBody body: RegisterDeviceRequest
    ): DeviceTokenDto {
        return pushNotificationService.registerDevice(
            userId = requestUserId,
            token = body.token,
            platform = body.platform.toPlatform()
        ).toDeviceTokenDto()
    }

    @DeleteMapping("/{token}")
    fun unregisterDeviceToken(
        @PathVariable token: String
    ) {
        return pushNotificationService.unregisterDevice(token)
    }
}