package com.example.travel.service

import com.example.travel.model.User
import com.example.travel.model.KakaoUserInfo
import com.example.travel.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

@Service
class KakaoAuthService(
    private val userRepository: UserRepository
) {

    @Value("\${kakao.client-id}")
    private lateinit var clientId: String

    @Value("\${kakao.redirect-uri}")
    private lateinit var redirectUri: String

    private val restTemplate = RestTemplate()

    fun getKakaoAccessToken(code: String): String {
        val uri = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/token")
            .queryParam("grant_type", "authorization_code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("code", code)
            .build()
            .toUri()

        val response = restTemplate.postForEntity(uri, null, Map::class.java)
        return response.body?.get("access_token") as? String
            ?: throw IllegalStateException("Access token을 가져올 수 없습니다.")
    }

    fun getKakaoUserInfo(accessToken: String): KakaoUserInfo {
        val uri = "https://kapi.kakao.com/v2/user/me"
        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }
        val entity = HttpEntity(null, headers)
    
        val response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map::class.java)
        val body = response.body ?: throw IllegalStateException("사용자 정보를 가져올 수 없습니다.")
    
        val kakaoId = body["id"].toString()
        val kakaoAccount = body["kakao_account"] as? Map<*, *> ?: throw IllegalStateException("Kakao 계정 정보를 가져올 수 없습니다.")
        val email = kakaoAccount["email"].toString()
        val properties = body["properties"] as? Map<*, *> ?: emptyMap<String, String>()
        val nickname = properties["nickname"].toString()
    
        return KakaoUserInfo(kakaoId, email, nickname)
    }
    
    
}
