package com.plcoding.chirp.service

import com.plcoding.chirp.domain.model.DeviceToken
import com.plcoding.chirp.domain.model.PushNotification
import com.plcoding.chirp.domain.type.ChatId
import com.plcoding.chirp.domain.type.UserId
import com.plcoding.chirp.exception.InvalidDeviceTokenException
import com.plcoding.chirp.infra.database.DeviceTokenEntity
import com.plcoding.chirp.infra.database.DeviceTokenRepository
import com.plcoding.chirp.infra.mappers.toDeviceToken
import com.plcoding.chirp.infra.mappers.toPlatformEntity
import com.plcoding.chirp.infra.push_notification.FirebasePushNotificationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentSkipListMap

@Service
class PushNotificationService(
    private val deviceTokenRepository: DeviceTokenRepository,
    private val firebasePushNotificationService: FirebasePushNotificationService
) {

    companion object {
        private val RETRY_DELAY_SECONDS = listOf(
            30L, 60L, 120L, 300L, 600L
        )
        const val MAX_RETRY_AGE_MINUTES = 30L
    }

    private val retryQueue = ConcurrentSkipListMap<Long, MutableList<RetryData>>()

    private val logger = LoggerFactory.getLogger(PushNotificationService::class.java)

    @Transactional
    fun registerDevice(
        userId: UserId,
        token: String,
        platform: DeviceToken.Platform
    ): DeviceToken {
        val existing = deviceTokenRepository.findByToken(token)

        val trimmedToken = token.trim()
        if (existing == null && !firebasePushNotificationService.isValidToken(trimmedToken)) {
            throw InvalidDeviceTokenException()
        }

        val entity = if (existing != null) {
            deviceTokenRepository.save(
                existing.apply {
                    this.userId = userId
                }
            )
        } else {
            deviceTokenRepository.save(
                DeviceTokenEntity(
                    userId = userId,
                    token = trimmedToken,
                    platform = platform.toPlatformEntity()
                )
            )
        }
        return entity.toDeviceToken()
    }

    @Transactional
    fun unregisterDevice(token: String) {
        deviceTokenRepository.deleteByToken(token.trim())
    }

    fun sendNewMessageNotifications(
        recipientUserIds: List<UserId>,
        senderUserId: UserId,
        senderUsername: String,
        message: String,
        chatId: ChatId
    ) {
        val deviceTokens = deviceTokenRepository.findByUserIdIn(recipientUserIds)
        if (deviceTokens.isEmpty()) {
            logger.info("No device tokens found for user ids: $recipientUserIds")
            return
        }

        val recipients = deviceTokens
            .filter { it.userId != senderUserId }
            .map { it.toDeviceToken() }

        val notification = PushNotification(
            title = "New message from $senderUsername",
            message = message,
            recipients = recipients,
            chatId = chatId,
            data = mapOf(
                "chatId" to chatId.toString(),
                "type" to "new_message"
            )
        )

        firebasePushNotificationService.sendNotification(notification)
    }

    fun sendWithRetry(
        notification: PushNotification,
        attempt: Int = 0
    ) {
        val result = firebasePushNotificationService.sendNotification(notification)

        result.permanentFailure.forEach {
            deviceTokenRepository.deleteByToken(it.token)
        }

        if (result.temporaryFailure.isNotEmpty() && attempt < RETRY_DELAY_SECONDS.size) {
            val retryNotification = notification.copy(recipients = result.temporaryFailure)
            scheduleRetry(retryNotification, attempt + 1)
        }

        if (result.succeeded.isNotEmpty()) {
            logger.info("Successfully sent ${result.succeeded.size} notifications")
        }
    }

    private fun scheduleRetry(notification: PushNotification, attempt: Int) {
        val delay = RETRY_DELAY_SECONDS.getOrElse(attempt - 1) {
            RETRY_DELAY_SECONDS.last()
        }
        val executedAt = Instant.now().plusSeconds(delay)
        val executeAtMillis = executedAt.toEpochMilli()

        val retryData = RetryData(notification, attempt, Instant.now())

        retryQueue.compute(executeAtMillis) { _, retries ->
            (retries ?: mutableListOf()).apply { add(retryData) }
        }

        logger.info("Scheduled retry for notification ${notification.id} at $executedAt")
    }

    @Scheduled(fixedDelay = 15_000L)
    fun processRetries() {
        val nowMillis = Instant.now().toEpochMilli()

        val toProcess = retryQueue.headMap(nowMillis, true)

        if (toProcess.isEmpty()) {
            return
        } else {
            val entries = toProcess.entries.toList()
            entries.forEach { (timeMillis, retries) ->
                retryQueue.remove(timeMillis)
                retries.forEach { retry ->
                    try {
                        val age = Duration.between(retry.createdAt, Instant.now()).toMinutes()
                        if (age > MAX_RETRY_AGE_MINUTES) {
                            logger.warn("Retry for notification ${retry.notification.id} is too old, skipping")
                            return@forEach
                        }
                        sendWithRetry(retry.notification, retry.attempt)
                    } catch (e: Exception) {
                        logger.warn("Failed to send retry for notification ${retry.notification.id}", e)
                    }
                }
            }
        }
    }

    private data class RetryData(
        val notification: PushNotification,
        val attempt: Int,
        val createdAt: Instant
    )
}