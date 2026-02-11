package com.plcoding.chirp.domain.exception

class InvalidProfilePictureException(
    override val message: String? = "Invalid profile picture"
) : RuntimeException(message)