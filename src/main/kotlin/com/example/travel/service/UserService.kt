// UserService.kt
package com.example.travel.service

import com.example.travel.model.User
import com.example.travel.model.VerificationToken
import com.example.travel.repository.UserRepository
import com.example.travel.repository.VerificationTokenRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val tokenRepository: VerificationTokenRepository,
    private val emailService: EmailService,
    private val passwordEncoder: BCryptPasswordEncoder
) {

  
    fun registerUser(email: String, password: String,nickName:String): User {
        val encodedPassword = if (!password.isNullOrEmpty()) {
            passwordEncoder.encode(password)
        } else {
            throw IllegalArgumentException("Password cannot be null or empty")
        }
    
        val user = userRepository.save(
            User(
                email = email,
                hashedPassword = encodedPassword,
                nickName=nickName
            )
        )
    

        // 이메일 인증 토큰 생성
        val token = VerificationToken(user = user)
        tokenRepository.save(token)

        // 인증 이메일 전송
        val verificationUrl = "http://localhost:8080/auth/verify?token=${token.token}"
        emailService.sendEmail(
            to = user.email,
            subject = "이메일 인증 요청",
            text = """
                <p>회원가입을 완료하려면 아래 링크를 클릭해 주세요:</p>
                <p><a href="$verificationUrl">이메일 인증하기</a></p>
            """.trimIndent()
        )

        return user
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun activateUser(token: String): Boolean {
        val verificationToken = tokenRepository.findByToken(token) ?: return false

        // 토큰이 유효한지 확인
        if (verificationToken.expiryDate.isBefore(LocalDateTime.now())) {
            return false // 만료된 토큰
        }

        // 사용자 활성화
        val user = verificationToken.user
        user.isActive = true
        userRepository.save(user)

   
        tokenRepository.delete(verificationToken)
        
        return true
    }

    fun isPasswordMatch(rawPassword: String, hashedPassword: String?): Boolean {
        if (hashedPassword.isNullOrEmpty()) {
            return false
        }
        return passwordEncoder.matches(rawPassword, hashedPassword)
    }

    fun registerOrLoginWithKakao(kakaoId: String, email: String, nickname: String): User {
        val existingUser = userRepository.findByKakaoId(kakaoId)
            ?: userRepository.findByEmail(email)
    
        return if (existingUser == null) {
            // 신규 사용자 등록
            val newUser = User(
                kakaoId = kakaoId,
                email = email,
                nickName = nickname,
                isActive = true // 카카오는 이메일 인증 불필요
            )
            userRepository.save(newUser)
        } else {
            existingUser
        }
    }
    
}
