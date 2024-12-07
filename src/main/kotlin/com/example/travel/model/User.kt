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

    @Column(nullable = true, unique = true)
    val kakaoId: String? = null, // 카카오 사용자 ID

    @Column(name = "hashed_password", nullable = true)
    var hashedPassword: String?=null,

    @Column(name = "is_active")
    var isActive: Boolean = false ,// 이메일 인증 여부 확인

    @Column(nullable = false, unique = true)
    val nickName: String // 닉네임
)


data class RegisterRequest(
    val email: String,
    val password: String,
    val nickName:String
)

data class KakaoUserInfo(
    val kakaoId: String,
    val email: String,
    val nickname: String
)

data class LoginRequest(
    val email: String,
    val password: String,
 
)

