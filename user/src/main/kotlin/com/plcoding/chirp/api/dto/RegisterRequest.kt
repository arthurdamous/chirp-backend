package com.plcoding.chirp.api.dto

import com.plcoding.chirp.api.util.Password
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length
import jakarta.validation.constraints.Pattern

data class RegisterRequest(
    @field:Email(message = "Must be a valid email address")
    val email: String,
    @field:Length(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    val username: String,
    @field:Password
    val password: String
)
