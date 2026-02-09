package com.plcoding.chirp.api.controllers

import com.plcoding.chirp.api.dto.ChatDto
import com.plcoding.chirp.api.dto.ChatMessageDto
import com.plcoding.chirp.api.dto.CreateChatRequest
import com.plcoding.chirp.api.dto.ParticipantToChatDto
import com.plcoding.chirp.api.mappers.toChatDto
import com.plcoding.chirp.api.util.requestUserId
import com.plcoding.chirp.domain.type.ChatId
import com.plcoding.chirp.service.ChatService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/chat")
class ChatController(private val chatService: ChatService) {

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }

    @GetMapping("/{chatId}/messages")
    fun getMessagesForChat(
        @PathVariable chatId: ChatId,
        @RequestParam(required = false) before: Instant? = null,
        @RequestParam(required = false) pageSize: Int = DEFAULT_PAGE_SIZE
    ): List<ChatMessageDto> {
        return chatService.getChatMessages(
            chatId,
            before,
            pageSize
        )
    }

    @GetMapping("/{chatId}")
    fun getChat(
        @PathVariable("chatId") chatId: ChatId
    ): ChatDto {
        return chatService.getChatById(
            chatId,
            requestUserId
        )?.toChatDto() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @GetMapping
    fun getChatsForUser(): List<ChatDto> {
        return chatService.findChatsByUser(
            userId = requestUserId,
        ).map { it.toChatDto() }
    }

    @PostMapping
    fun createChat(
        @Valid @RequestBody body: CreateChatRequest
    ): ChatDto {
        return chatService.createChat(
            creatorId = requestUserId,
            otherUserIds = body.otherUserIds.toSet()
        ).toChatDto()
    }

    @PostMapping("/{chatId}/add")
    fun addParticipantsToChat(
        @Valid @RequestBody body: ParticipantToChatDto,
        @PathVariable chatId: ChatId
    ): ChatDto {
        return chatService.addParticipantsToChat(
            requestUserId = requestUserId,
            userIds = body.userIds.toSet(),
            chatId = chatId
        ).toChatDto()
    }

    @DeleteMapping("/{chatId}/leave")
    fun leaveChat(
        @PathVariable chatId: ChatId
    ) {
        return chatService.removeParticipantsFromChat(
            chatId = chatId,
            userId = requestUserId
        )
    }
}