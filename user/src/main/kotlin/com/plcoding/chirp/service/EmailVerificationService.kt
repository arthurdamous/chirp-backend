package com.plcoding.chirp.service

import com.plcoding.chirp.domain.exception.InvalidTokenException
import com.plcoding.chirp.domain.exception.UserNotFoundException
import com.plcoding.chirp.domain.model.EmailVerificationToken
import com.plcoding.chirp.infra.database.entities.EmailVerificationTokenEntity
import com.plcoding.chirp.infra.database.mappers.toEmailVerificationToken
import com.plcoding.chirp.infra.database.repositories.EmailVerificationTokenRepository
import com.plcoding.chirp.infra.database.repositories.UserRepository
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
    @param:Value("\${chirp.email.verification.expiry-hours}") private val expiryHours: Long
) {

    fun resendEmailVerification(
        email: String
    ) {
        //TODO: trigger
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
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanUpExpiredTokens() {
        emailVerificationTokenRepository.deleteByExpiresAtLessThan(
            now = Instant.now()
        )
    }
}