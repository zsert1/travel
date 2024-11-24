package com.example.travel.controller

import com.example.travel.security.JwtTokenProvider
import com.example.travel.service.UserService
import com.example.travel.service.KakaoAuthService
import com.example.travel.model.RegisterRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@CrossOrigin("*") // 모든 도메인 허용
@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val kakaoAuthService:KakaoAuthService
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

    userService.registerUser(request.email, request.password,request.nickName)
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
    
        // 비밀번호가 없는 경우 (예: SSO 사용자)
        if (user.hashedPassword.isNullOrEmpty()) {
            return ResponseEntity(mapOf("error" to "이 계정은 비밀번호로 로그인할 수 없습니다."), HttpStatus.UNAUTHORIZED)
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

    @PostMapping("/kakao/login")
    fun kakaoLogin(@RequestParam code: String): ResponseEntity<Map<String, String>> {
        return try {
            // 1. 카카오 액세스 토큰 가져오기
            val accessToken = kakaoAuthService.getKakaoAccessToken(code)
    
            // 2. 카카오 사용자 정보 가져오기
            val kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(accessToken)
    
            // 3. 회원가입 또는 로그인 처리
            val user = userService.registerOrLoginWithKakao(
                kakaoId = kakaoUserInfo.kakaoId,
                email = kakaoUserInfo.email,
                nickname = kakaoUserInfo.nickname
            )
    
            // 4. JWT 토큰 생성
            val accessTokenJwt = jwtTokenProvider.createAccessToken(user.email)
            val refreshTokenJwt = jwtTokenProvider.createRefreshToken(user.email)
    
            ResponseEntity.ok(
                mapOf(
                    "accessToken" to accessTokenJwt,
                    "refreshToken" to refreshTokenJwt
                )
            )
        } catch (e: Exception) {
            ResponseEntity(mapOf("error" to (e.message ?: "Unknown error")), HttpStatus.UNAUTHORIZED)
        }
    }
    
    

}
