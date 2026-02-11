package com.plcoding.chirp.infra.web.storage

import com.plcoding.chirp.domain.exception.InvalidProfilePictureException
import com.plcoding.chirp.domain.exception.StorageException
import com.plcoding.chirp.domain.models.ProfilePictureUploadCredentials
import com.plcoding.chirp.domain.type.UserId
import org.hibernate.validator.constraints.UUID
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.util.MimeType
import org.springframework.web.client.RestClient
import java.time.Instant

@Service
class SupabaseStorageService(
    @param:Value("\${supabase.url}") val url: String,
    private val supabaseRestClient: RestClient
) {

    companion object {
        private val allowedMimeTypes = mapOf(
            "image/jpeg" to "jpg",
            "image/png" to "png",
            "image/jpg" to "jpg",
            "image/webp" to "webp"
        )
    }

    fun generateSignedUploadUrl(userId: UserId, mimeType: String): ProfilePictureUploadCredentials {
        val extension =
            allowedMimeTypes[mimeType] ?: throw InvalidProfilePictureException("Unsupported mime type $mimeType")

        val fileName = "user_${userId}_${java.util.UUID.randomUUID()}.$extension"
        val path = "profile-pictures/$fileName"
        val publicUrl = "$url/storage/v1/object/public/$path"

        return ProfilePictureUploadCredentials(
            uploadUrl = createSignedUrl(path, 3600),
            publicUrl = publicUrl,
            headers = mapOf("Content-Type" to mimeType),
            expiresAt = Instant.now().plusSeconds(3600)
        )
    }

    fun deleteFile(
        url: String
    ) {
        val path = if (url.contains("/object/public/")) {
            url.substringAfter("/object/public/")
        } else throw StorageException("Invalid url")

        val deleteUrl = "/storage/v1/object/$path"

        val response = supabaseRestClient
            .delete()
            .uri(deleteUrl)
            .retrieve()
            .toBodilessEntity()

        if (response.statusCode.isError) throw StorageException("Failed to delete file ${response.statusCode.value()}")
    }

    private fun createSignedUrl(
        path: String,
        expiresInSeconds: Int
    ): String {
        val json = """
            { "expiresInSeconds": $expiresInSeconds }
            """.trimIndent()

        val response = supabaseRestClient
            .post()
            .uri("/storage/v1/object/upload/sign/$path")
            .body(json)
            .header("Content-Type", "application/json")
            .retrieve()
            .body(SignedUploadResponse::class.java)
            ?: throw StorageException("Failed to create signed upload url")

        return "$url/storage/v1${response.url}"
    }

    private data class SignedUploadResponse(
        val url: String
    )


}