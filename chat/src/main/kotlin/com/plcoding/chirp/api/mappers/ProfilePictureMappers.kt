package com.plcoding.chirp.api.mappers

import com.plcoding.chirp.api.dto.PictureUploadResponse
import com.plcoding.chirp.domain.models.ProfilePictureUploadCredentials

fun ProfilePictureUploadCredentials.toResponse() = PictureUploadResponse(
    uploadUrl = uploadUrl,
    publicUrl = publicUrl,
    headers = headers,
    expiresAt = expiresAt
)