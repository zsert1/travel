// SecurityConfig.kt
package com.example.travel.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.authentication.AuthenticationManager

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf(AbstractHttpConfigurer<*, *>::disable)
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/auth/register", "/auth/test", "/auth/verify","/auth/login").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { form -> form.disable() }

        return http.build()
    }

    // 테스트용 Security 설정
    @Profile("test")
    @Bean
    fun testFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors().and()
            .csrf().disable()
            .authorizeHttpRequests { requests ->
                requests.anyRequest().permitAll() // 모든 요청 허용
            }
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        return http.build()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}
