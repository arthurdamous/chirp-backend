package com.plcoding.chirp.service.auth

import com.plcoding.chirp.domain.exception.UserAlreadyExistsException
import com.plcoding.chirp.domain.model.User
import com.plcoding.chirp.infra.database.entities.UserEntity
import com.plcoding.chirp.infra.database.mappers.toUser
import com.plcoding.chirp.infra.security.PasswordEncoder
import com.plcoding.chirp.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(email: String, username: String, password: String): User {
        val user = userRepository.findByEmailOrUsername(
            email = email.trim(),
            username = username.trim()
        )
        if (user != null) {
            throw UserAlreadyExistsException()
        }
        val savedUser = userRepository.save(
            UserEntity(
                email = email.trim(),
                username = username.trim(),
                hashedPassword = passwordEncoder.encode(password)
            )
        ).toUser()
        return savedUser
    }

}