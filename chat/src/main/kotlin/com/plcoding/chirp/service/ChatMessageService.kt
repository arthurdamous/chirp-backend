package com.plcoding.chirp.service

import com.plcoding.chirp.domain.event.MessageDeletedEvent
import com.plcoding.chirp.domain.events.chat.ChatEvent
import com.plcoding.chirp.domain.exception.ChatNotFoundException
import com.plcoding.chirp.domain.exception.ChatParticipantNotFoundException
import com.plcoding.chirp.domain.exception.ForbiddenException
import com.plcoding.chirp.domain.exception.MessageNotFoundException
import com.plcoding.chirp.domain.models.ChatMessage
import com.plcoding.chirp.domain.type.ChatId
import com.plcoding.chirp.domain.type.ChatMessageId
import com.plcoding.chirp.domain.type.UserId
import com.plcoding.chirp.infra.database.entities.ChatMessageEntity
import com.plcoding.chirp.infra.database.mappers.toChatMessage
import com.plcoding.chirp.infra.database.repositories.ChatMessageRepository
import com.plcoding.chirp.infra.database.repositories.ChatParticipantRepository
import com.plcoding.chirp.infra.database.repositories.ChatRepository
import com.plcoding.chirp.infra.message_queue.EventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatMessageService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val eventPublisher: EventPublisher,
) {

    @Transactional
    fun sendMessage(
        chatId: ChatId,
        senderId: UserId,
        content: String,
        messageId: ChatMessageId? = null
    ): ChatMessage {
        val chat = chatRepository.findChatById(chatId, senderId)
            ?: throw ChatNotFoundException()

        val sender = chatParticipantRepository.findByIdOrNull(senderId)
            ?: throw ChatParticipantNotFoundException(senderId)

        val savedMessage = chatMessageRepository.save(
            ChatMessageEntity(
                chatId = chatId,
                sender = sender,
                id = messageId,
                content = content.trim(),
                chat = chat
            )
        )

        eventPublisher.publish(
            event = ChatEvent.NewMessage(
                senderId = senderId,
                senderUsername = sender.username,
                recipientIds = chat.participants.map { it.userId }.toSet(),
                chatId = chatId,
                message = savedMessage.content
            )
        )

        return savedMessage.toChatMessage()
    }

    @Transactional
    fun deleteMessage(requestUserId: UserId, messageId: ChatMessageId) {
        val message = chatMessageRepository.findByIdOrNull(messageId)
            ?: throw MessageNotFoundException(messageId)

        if (message.sender.userId != requestUserId) {
            throw ForbiddenException()
        }

        chatMessageRepository.delete(message)

        applicationEventPublisher.publishEvent(MessageDeletedEvent(message.chatId, messageId))
    }
}