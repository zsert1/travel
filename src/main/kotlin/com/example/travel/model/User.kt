package com.example.travel.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(name = "hashed_password", nullable = false)
    var hashedPassword: String,

    @Column(name = "is_active")
    var isActive: Boolean = false // 이메일 인증 여부 확인
)


data class RegisterRequest(
    val email: String,
    val password: String
)
