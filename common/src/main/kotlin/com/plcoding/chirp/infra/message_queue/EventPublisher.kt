package com.plcoding.chirp.infra.message_queue

import com.plcoding.chirp.domain.events.ChirpEvent
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class EventPublisher(
    private val rabbitTemplate: RabbitTemplate
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun <T: ChirpEvent> publish(event: T) {
        try {
            rabbitTemplate.convertAndSend(event.exchange, event.eventKey, event)
            logger.info("Publishing event: $event")
        } catch (e: Exception) {
            logger.error("Failed to publish event: $event", e)

        }
    }
}