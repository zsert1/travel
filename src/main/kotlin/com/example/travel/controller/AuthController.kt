// AuthController.kt
package com.example.travel.controller

import com.example.travel.security.JwtTokenProvider
import com.example.travel.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/register")
    fun registerUser(@RequestParam email: String, @RequestParam password: String): ResponseEntity<String> {
        val existingUser = userService.findByEmail(email)
        if (existingUser != null) {
            return ResponseEntity("이미 존재하는 이메일입니다.", HttpStatus.BAD_REQUEST)
        }

        userService.registerUser(email, password)
        return ResponseEntity("회원가입 성공: 이메일 인증을 완료해 주세요.", HttpStatus.CREATED)
    }

    @PostMapping("/login")
    fun loginUser(@RequestParam email: String, @RequestParam password: String): ResponseEntity<Map<String, String>> {
        val user = userService.findByEmail(email)
            ?: return ResponseEntity(mapOf("error" to "사용자를 찾을 수 없습니다."), HttpStatus.UNAUTHORIZED)

        if (!user.isActive) {
            return ResponseEntity(mapOf("error" to "이메일 인증이 필요합니다."), HttpStatus.UNAUTHORIZED)
        }

        // UserService의 isPasswordMatch 메서드를 사용하여 비밀번호 검증
        val isPasswordMatch = userService.isPasswordMatch(password, user.hashedPassword)
        if (!isPasswordMatch) {
            return ResponseEntity(mapOf("error" to "비밀번호가 일치하지 않습니다."), HttpStatus.UNAUTHORIZED)
        }

        val accessToken = jwtTokenProvider.createAccessToken(email)
        val refreshToken = jwtTokenProvider.createRefreshToken(email)
        return ResponseEntity(
            mapOf(
                "accessToken" to accessToken,
                "refreshToken" to refreshToken
            ),
            HttpStatus.OK
        )
    }
}
