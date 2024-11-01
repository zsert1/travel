// SecurityConfig.kt
package com.example.travel.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors().and()
            .csrf().disable()
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/", "/auth/register", "/auth/verify").permitAll() // 기본 루트 경로와 인증 관련 경로 허용
                    .anyRequest().authenticated() // 다른 요청은 인증 필요
            }
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용하지 않음 (JWT 기반)
            .and()
            .formLogin().disable() // 기본 로그인 폼 비활성화

        return http.build()
    }
}
