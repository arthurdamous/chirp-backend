package com.plcoding.chirp.service

import com.plcoding.chirp.domain.event.ProfilePictureUpdatedEvent
import com.plcoding.chirp.domain.exception.ChatParticipantNotFoundException
import com.plcoding.chirp.domain.exception.InvalidProfilePictureException
import com.plcoding.chirp.domain.models.ProfilePictureUploadCredentials
import com.plcoding.chirp.domain.type.UserId
import com.plcoding.chirp.infra.database.repositories.ChatParticipantRepository
import com.plcoding.chirp.infra.web.storage.SupabaseStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfilePictureService(
    private val supabaseStorageService: SupabaseStorageService,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    @param:Value("\${supabase.url}") val url: String,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun generateUploadCredentials(
        userId: UserId,
        mimeType: String
    ): ProfilePictureUploadCredentials {
        return supabaseStorageService.generateSignedUploadUrl(userId, mimeType)
    }

    @Transactional
    fun deleteProfilePicture(
        userId: UserId
    ) {
        val chatParticipant =
            chatParticipantRepository.findByIdOrNull(userId) ?: throw ChatParticipantNotFoundException(userId)


        chatParticipant.profilePictureUrl?.let { url ->
            chatParticipantRepository.save(
                chatParticipant.apply { profilePictureUrl = null }
            )

            supabaseStorageService.deleteFile(url)

            applicationEventPublisher.publishEvent(
                ProfilePictureUpdatedEvent(
                    userId = userId,
                    newUrl = null
                )
            )
        }
    }

    @Transactional
    fun confirmProfilePictureUpload(userId: UserId, publicUrl: String) {
        val chatParticipant =
            chatParticipantRepository.findByIdOrNull(userId) ?: throw ChatParticipantNotFoundException(userId)

        val oldUrl = chatParticipant.profilePictureUrl

        chatParticipantRepository.save(
            chatParticipant.apply { profilePictureUrl = publicUrl }
        )

        try {
            oldUrl?.let { supabaseStorageService.deleteFile(it) }
        } catch (e: Exception) {
            logger.warn("Failed to update profile picture url for user $userId", e)
        }

        applicationEventPublisher.publishEvent(
            ProfilePictureUpdatedEvent(
                userId = userId,
                newUrl = publicUrl
            )
        )
    }
}