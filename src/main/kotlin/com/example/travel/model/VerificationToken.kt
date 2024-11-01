package com.example.travel.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "verification_tokens")
data class VerificationToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // 고유한 인증 토큰
    @Column(nullable = false, unique = true)
    val token: String = UUID.randomUUID().toString(), 
    // 연결된 user
    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User,

    val expiryDate: LocalDateTime = LocalDateTime.now().plusHours(24) // 24시간 유효
)
