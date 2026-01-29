package com.plcoding.chirp.service

import com.plcoding.chirp.domain.events.user.UserEvent
import com.plcoding.chirp.domain.exception.InvalidTokenException
import com.plcoding.chirp.domain.exception.UserNotFoundException
import com.plcoding.chirp.domain.model.EmailVerificationToken
import com.plcoding.chirp.infra.database.entities.EmailVerificationTokenEntity
import com.plcoding.chirp.infra.database.mappers.toEmailVerificationToken
import com.plcoding.chirp.infra.database.mappers.toUser
import com.plcoding.chirp.infra.database.repositories.EmailVerificationTokenRepository
import com.plcoding.chirp.infra.database.repositories.UserRepository
import com.plcoding.chirp.infra.message_queue.EventPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class EmailVerificationService(
    private val emailVerificationTokenRepository: EmailVerificationTokenRepository,
    private val userRepository: UserRepository,
    @param:Value("\${chirp.email.verification.expiry-hours}") private val expiryHours: Long,
    private val eventPublisher: EventPublisher
) {

    @Transactional
    fun resendEmailVerification(
        email: String
    ) {
        val token = createVerificationToken(email)

        if (token.user.hasEmailVerified) {
            return
        }

        eventPublisher.publish(
            event = UserEvent.RequestResendVerification(
                userId = token.user.id,
                email = token.user.email,
                username = token.user.username,
                verificationToken = token.token
            )
        )
    }

    @Transactional
    fun createVerificationToken(email: String): EmailVerificationToken {
        val userEntity = userRepository.findByEmail(email)
            ?: throw UserNotFoundException()

        emailVerificationTokenRepository.invalidateActiveTokensForUser(userEntity)

        val token = EmailVerificationTokenEntity(
            expiresAt = Instant.now().plus(expiryHours, ChronoUnit.HOURS),
            user = userEntity
        )
        return emailVerificationTokenRepository.save(token).toEmailVerificationToken()
    }

    @Transactional
    fun verifyEmail(token: String) {
        val verificationToken = emailVerificationTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Invalid token")

        if (verificationToken.isUsed) {
            throw InvalidTokenException("Email verification token is already used")
        }

        if (verificationToken.isExpired) {
            throw InvalidTokenException("Email verification token is expired")
        }

        emailVerificationTokenRepository.save(
            verificationToken.apply {
                this.usedAt = Instant.now()
            }
        )

        userRepository.save(
            verificationToken.user.apply {
                this.hasVerifiedEmail = true
            }
        ).toUser()

        eventPublisher.publish(
            event = UserEvent.Verified(
                userId = verificationToken.user.id!!,
                email = verificationToken.user.email,
                username = verificationToken.user.username
            )
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanUpExpiredTokens() {
        emailVerificationTokenRepository.deleteByExpiresAtLessThan(
            now = Instant.now()
        )
    }
}