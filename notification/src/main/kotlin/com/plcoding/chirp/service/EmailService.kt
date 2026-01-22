package com.plcoding.chirp.service

import com.plcoding.chirp.domain.type.UserId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration

@Service
class EmailService(
    private val javaMailSender: JavaMailSender,
    private val emailTemplateService: EmailTemplateService,
    @param:Value("\${chirp.email.from}") private val emailFrom: String,
    @param:Value("\${chirp.email.url}") private val baseUrl: String
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendVerificationEmail(
        email: String,
        username: String,
        userId: UserId,
        token: String
    ) {
        logger.info("Sending verification email to $email for user $username with ID $userId")

        val verificationUrl = UriComponentsBuilder
            .fromUriString("$baseUrl/api/auth/verify")
            .queryParam("token", token)
            .build()
            .toUriString()

        val htmlContent = emailTemplateService.processTemplate(
            templateName = "emails/account-verification",
            model = mapOf(
                "username" to username,
                "verificationUrl" to verificationUrl
            )
        )

        sendHtmlEmail(email, "Verify your Chirp account", htmlContent)
    }


    fun sendPasswordResetEmail(
        email: String,
        username: String,
        userId: UserId,
        token: String,
        expiresIn: Duration
    ) {
        logger.info("Sending password reset email to $email for user $username with ID $userId")

        val resetPasswordUrl = UriComponentsBuilder
            .fromUriString("$baseUrl/api/auth/reset-password")
            .queryParam("token", token)
            .build().toUriString()

        val htmlContent = emailTemplateService.processTemplate(
            templateName = "emails/reset-password",
            model = mapOf(
                "username" to username,
                "resetPasswordUrl" to resetPasswordUrl,
                "expiresIn" to "${expiresIn.seconds / 60} minutes"
            )
        )

        sendHtmlEmail(email, "Reset your Chirp password", htmlContent)
    }


    private fun sendHtmlEmail(
        to: String,
        subject: String,
        html: String
    ) {
        val message = javaMailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setFrom(emailFrom)
            setTo(to)
            setSubject(subject)
            setText(html, true)
        }
        try {
            javaMailSender.send(message)
        } catch (e: Exception) {
            logger.error("Failed to send email to $to", e)
        }
    }
}