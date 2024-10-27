// UserService.kt
package com.example.travel.service

import com.example.travel.model.User
import com.example.travel.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    fun registerUser(email: String, password: String): User {
        val encodedPassword = passwordEncoder.encode(password)
        val user = User(email = email, hashedPassword = encodedPassword)
        return userRepository.save(user)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun activateUser(user: User) {
        user.isActive = true
        userRepository.save(user)
    }
}
