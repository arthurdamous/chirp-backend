package com.plcoding.chirp.api.controller

import com.plcoding.chirp.api.dto.AuthenticatedUserDto
import com.plcoding.chirp.api.dto.ChangePasswordRequest
import com.plcoding.chirp.api.dto.EmailRequest
import com.plcoding.chirp.api.dto.LoginRequest
import com.plcoding.chirp.api.dto.RefreshRequest
import com.plcoding.chirp.api.dto.RegisterRequest
import com.plcoding.chirp.api.dto.ResetPasswordRequest
import com.plcoding.chirp.api.dto.UserDto
import com.plcoding.chirp.api.mappers.toAuthenticatedUserDto
import com.plcoding.chirp.api.mappers.toUserDto
import com.plcoding.chirp.infra.rate_limiting.EmailRateLimiter
import com.plcoding.chirp.service.AuthService
import com.plcoding.chirp.service.EmailVerificationService
import com.plcoding.chirp.service.PasswordResetService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val passwordResetService: PasswordResetService,
    private val emailRateLimiter: EmailRateLimiter
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody body: RegisterRequest
    ): UserDto {
        return authService.register(
            email = body.email,
            username = body.username,
            password = body.password,
        ).toUserDto()
    }

    @PostMapping("/login")
    fun login(
        @RequestBody body: LoginRequest,
    ): AuthenticatedUserDto {
        return authService.login(
            email = body.email,
            password = body.password
        ).toAuthenticatedUserDto()
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: RefreshRequest
    ): AuthenticatedUserDto {
        return authService.refresh(body.refreshToken).toAuthenticatedUserDto()
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody body: RefreshRequest
    ) {
        authService.logout(body.refreshToken)
    }

    @GetMapping("/verify")
    fun verifyEmail(
        @RequestParam token: String,
    ) {
        emailVerificationService.verifyEmail(token)
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @Valid @RequestBody body: ResetPasswordRequest
    ) {
        passwordResetService.resetPassword(
            token = body.token,
            newPassword = body.newPassword
        )
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(
        @Valid @RequestBody body: EmailRequest
    ) {
        passwordResetService.requestPasswordReset(body.email)
    }

    @PostMapping("/change-password")
    fun changePassword(
        @Valid @RequestBody body: ChangePasswordRequest
    ) {
        //TODO: Implement this
    }

    @PostMapping("/resend-verification")
    fun resendVerification(
        @Valid @RequestBody body: EmailRequest
    ) {
        emailRateLimiter.withRateLimit(
            email = body.email,
        ) {
            emailVerificationService.resendEmailVerification(body.email)
        }
    }

}