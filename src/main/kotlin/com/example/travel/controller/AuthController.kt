package com.example.travel.controller

import com.example.travel.security.JwtTokenProvider
import com.example.travel.service.UserService
import com.example.travel.model.RegisterRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@CrossOrigin("*") // 모든 도메인 허용
@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @GetMapping("/test")
    fun testEndpoint(): ResponseEntity<String> {
        println("Test endpoint called")
        return ResponseEntity("Test endpoint is working", HttpStatus.OK)
    }

 @PostMapping("/register")
 fun registerUser(@RequestBody request: RegisterRequest): ResponseEntity<String> {
    println("${request.email} email")
    val existingUser = userService.findByEmail(request.email)
    if (existingUser != null) {
        return ResponseEntity("이미 존재하는 이메일입니다.", HttpStatus.BAD_REQUEST)
    }

    userService.registerUser(request.email, request.password)
    return ResponseEntity("회원가입 성공: 이메일 인증을 완료해 주세요.", HttpStatus.CREATED)
}

    @GetMapping("/verify")
    fun verifyEmail(@RequestParam token: String): ResponseEntity<String> {
        val isVerified = userService.activateUser(token)
        return if (isVerified) {
            ResponseEntity("이메일 인증이 완료되었습니다.", HttpStatus.OK)
        } else {
            ResponseEntity("유효하지 않은 인증 토큰입니다.", HttpStatus.BAD_REQUEST)
        }
    }
    @PostMapping("/login")
    fun loginUser(@RequestBody request: RegisterRequest): ResponseEntity<Map<String, String>> {
        val user = userService.findByEmail(request.email)
            ?: return ResponseEntity(mapOf("error" to "사용자를 찾을 수 없습니다."), HttpStatus.UNAUTHORIZED)

        if (!user.isActive) {
            return ResponseEntity(mapOf("error" to "이메일 인증이 필요합니다."), HttpStatus.UNAUTHORIZED)
        }

        // UserService의 isPasswordMatch 메서드를 사용하여 비밀번호 검증
        val isPasswordMatch = userService.isPasswordMatch(request.password, user.hashedPassword)
        if (!isPasswordMatch) {
            return ResponseEntity(mapOf("error" to "비밀번호가 일치하지 않습니다."), HttpStatus.UNAUTHORIZED)
        }

        val accessToken = jwtTokenProvider.createAccessToken(request.email)
        val refreshToken = jwtTokenProvider.createRefreshToken(request.email)
        return ResponseEntity(
            mapOf(
                "accessToken" to accessToken,
                "refreshToken" to refreshToken
            ),
            HttpStatus.OK
        )
    }
}
