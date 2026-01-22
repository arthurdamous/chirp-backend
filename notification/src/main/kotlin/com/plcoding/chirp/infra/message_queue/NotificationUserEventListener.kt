package com.plcoding.chirp.infra.message_queue

import com.plcoding.chirp.domain.events.user.UserEvent
import com.plcoding.chirp.service.EmailService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import kotlin.time.toDuration

@Component
class NotificationUserEventListener(private val emailService: EmailService) {

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_USER_EVENTS])
    fun handleUserEvent(event: UserEvent) {
        when (event) {
            is UserEvent.Created -> {
                emailService.sendVerificationEmail(
                    email = event.email,
                    token = event.verificationToken,
                    userId = event.userId,
                    username = event.username
                )
            }

            is UserEvent.RequestResendVerification -> {
                emailService.sendVerificationEmail(
                    email = event.email,
                    token = event.verificationToken,
                    userId = event.userId,
                    username = event.username
                )
            }
            is UserEvent.RequestResetPassword -> {
                emailService.sendPasswordResetEmail(
                    email = event.email,
                    token = event.verificationToken,
                    userId = event.userId,
                    username = event.username,
                    expiresIn = Duration.ofMinutes(event.expiresInMinutes)
                )
            }
            else -> Unit
        }
    }
}