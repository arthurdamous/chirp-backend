package com.plcoding.chirp.service

import com.plcoding.chirp.domain.events.user.UserEvent
import com.plcoding.chirp.domain.exception.InvalidCredentialsException
import com.plcoding.chirp.domain.exception.InvalidTokenException
import com.plcoding.chirp.domain.exception.SamePasswordException
import com.plcoding.chirp.domain.exception.UserNotFoundException
import com.plcoding.chirp.domain.type.UserId
import com.plcoding.chirp.infra.database.entities.PasswordResetTokenEntity
import com.plcoding.chirp.infra.database.repositories.PasswordResetTokenRepository
import com.plcoding.chirp.infra.database.repositories.RefreshTokenRepository
import com.plcoding.chirp.infra.database.repositories.UserRepository
import com.plcoding.chirp.infra.message_queue.EventPublisher
import com.plcoding.chirp.infra.security.PasswordEncoder
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class PasswordResetService(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    @param:Value("\${chirp.email.reset-password.expiry-minutes}")
    private val expiryMinutes: Long,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val eventPublisher: EventPublisher
) {

    @Transactional
    fun requestPasswordReset(email: String) {
        val user = userRepository.findByEmail(email)
            ?: return

        passwordResetTokenRepository.invalidateActiveTokensForUser(user)

        val token = PasswordResetTokenEntity(
            user = user,
            expiresAt = Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES),
        )
        passwordResetTokenRepository.save(token)

        eventPublisher.publish(
            event = UserEvent.RequestResetPassword(
                userId = user.id!!,
                email = user.email,
                username = user.username,
                verificationToken = token.token,
                expiresInMinutes = expiryMinutes
            )
        )
    }

    fun resetPassword(token: String, newPassword: String) {
        val resetToken = passwordResetTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Invalid token")

        if (resetToken.isUsed) {
            throw InvalidTokenException("Email verification token is already used")
        }

        if (resetToken.isExpired) {
            throw InvalidTokenException("Email verification token is expired")
        }

        val user = resetToken.user

        if (passwordEncoder.matches(token, user.hashedPassword)) {
            throw SamePasswordException()
        }
        val hashedPassword = passwordEncoder.encode(newPassword)
        userRepository.save(
            user.apply {
                this.hashedPassword = hashedPassword
            }
        )

        passwordResetTokenRepository.save(
            resetToken.apply {
                this.usedAt = Instant.now()
            }
        )

        refreshTokenRepository.deleteByUserId(user.id!!)
    }

    @Transactional
    fun changePassword(
        userId: UserId,
        oldPassword: String,
        newPassword: String,
    ) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        if(!passwordEncoder.matches(oldPassword, user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        if(oldPassword == newPassword) {
            throw SamePasswordException()
        }

        refreshTokenRepository.deleteByUserId(user.id!!)

        val newHashedPassword = passwordEncoder.encode(newPassword)
        userRepository.save(
            user.apply {
                this.hashedPassword = newHashedPassword
            }
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanUpExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiresAtLessThan(
            now = Instant.now()
        )
    }

}