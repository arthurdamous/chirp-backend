package com.plcoding.chirp.api.mappers

import com.plcoding.chirp.api.dto.AuthenticatedUserDto
import com.plcoding.chirp.api.dto.UserDto
import com.plcoding.chirp.domain.model.AuthenticatedUser
import com.plcoding.chirp.domain.model.User


fun AuthenticatedUser.toAuthenticatedUserDto(): AuthenticatedUserDto {
    return AuthenticatedUserDto(
        user = user.toUserDto(),
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

fun User.toUserDto(): UserDto {
    return UserDto(
        id = id,
        username = username,
        email = email,
        hasVerifiedEmail = hasEmailVerified
    )
}