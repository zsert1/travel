package com.example.travel.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    @Value("\${jwt.access-token-validity}")
    private var accessTokenValidityInMilliseconds: Long = 0

    @Value("\${jwt.refresh-token-validity}")
    private var refreshTokenValidityInMilliseconds: Long = 0

    // Access Token 생성
    fun createAccessToken(email: String): String {
        val claims = Jwts.claims().setSubject(email)
        val now = Date()
        val validity = Date(now.time + accessTokenValidityInMilliseconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    // Refresh Token 생성
    fun createRefreshToken(email: String): String {
        val claims = Jwts.claims().setSubject(email)
        val now = Date()
        val validity = Date(now.time + refreshTokenValidityInMilliseconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }
}
