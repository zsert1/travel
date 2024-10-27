// JwtTokenProvider.kt
package com.example.travel.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider {
    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    @Value("\${jwt.validity}")
    private var validityInMilliseconds: Long = 0

    fun createToken(email: String): String {
        val claims = Jwts.claims().setSubject(email)
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }
}
