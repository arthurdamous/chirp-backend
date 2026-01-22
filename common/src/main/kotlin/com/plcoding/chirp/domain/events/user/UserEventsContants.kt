package com.plcoding.chirp.domain.events.user

object UserEventsContants {

    const val USER_EXCHANGE = "user.events"

    const val USER_CREATED_KEY = "user.created"
    const val USER_VERIFIED_EVENT = "user.verified"
    const val USER_REQUEST_RESEND_VERIFICATION = "user.request_resend_verification"
    const val USER_REQUEST_RESET_PASSWORD = "user.request_reset_password"
}