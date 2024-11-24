# Travel Application Backend

## 프로젝트 개요

Travel Application의 백엔드

---

## 기술 스택

- **언어**: Kotlin
- **프레임워크**: Spring Boot
- **데이터베이스**: MySQL
- **보안**: Spring Security (JWT 기반)
- **외부 연동**: Kakao OAuth API

---

## 프로젝트 구조

src/  
├── **controller**  
│ ├── `AuthController.kt` - 사용자 인증 및 카카오 로그인 컨트롤러  
├── **model**  
│ ├── `User.kt` - 사용자 엔티티 및 DTO  
│ ├── `VerificationToken.kt` - 이메일 인증 토큰 엔티티  
├── **service**  
│ ├── `UserService.kt` - 사용자 관련 로직  
│ ├── `EmailService.kt` - 이메일 전송 서비스  
│ ├── `KakaoAuthService.kt` - 카카오 로그인 연동 서비스  
├── **repository**  
│ ├── `UserRepository.kt` - 사용자 저장소  
│ ├── `VerificationTokenRepository.kt` - 인증 토큰 저장소  
├── **security**  
│ ├── `JwtTokenProvider.kt` - JWT 생성 및 검증

---

## 환경 설정

`application.yaml` 파일에 다음 설정을 추가.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/travel_app?useSSL=false
    username: root
    password: your_password

kakao:
  client-id: "your_kakao_client_id"
  redirect-uri: "http://localhost:8080/auth/kakao/callback"

jwt:
  secret-key: "your_secret_key"
  access-token-validity: 3600000 # 1시간
  refresh-token-validity: 1209600000 # 2주
```

### 1. 사용자 회원가입

- **URL**: `/auth/register`
- **Method**: `POST`
- **Request Body**:

```json
{
  "email": "user@example.com",
  "password": "password123",
  "nickName": "UserNickname"
}
```

- **Reponse**:

```json
201 Created:
{
"message": "회원가입 성공: 이메일 인증을 완료해 주세요."
}
```

```json
400 Bad Request:
{
  "message": "이미 존재하는 이메일입니다."
}
```

### 2. 이메일 인증

- **URL**: `/auth/verify`
- **Method**: `GET`
- **Query Parameter**:
  - token: 이메일 인증 토큰
- **Reponse**:

```json
200 OK:
{
  "message": "이메일 인증이 완료되었습니다."
}
```

```json
400 Bad Request:
{
  "message": "유효하지 않은 인증 토큰입니다."
}
```

### 3. 사용자 로그인

- **URL**: `/auth/login`
- **Method**: `POST`
- **Request Body**:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

- **Reponse**:

```json
200 ok:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR...",
  "refreshToken": "dGhpc2lzYXJlZnJlc2h..."
}
```

```json
401 Unauthorized:
{
  "error": "사용자를 찾을 수 없습니다."
}
```

### 4. 카카오 로그인

- **URL**: `/auth/kakao/login`
- **Method**: `POST`
- **Query Parameter**:

  - `code`: 카카오에서 제공받은 인가 코드

- **Reponse**:

```json
200 ok:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR...",
  "refreshToken": "dGhpc2lzYXJlZnJlc2h..."
}
```

```json
401 Unauthorized:
{
  "error": "사용자를 찾을 수 없습니다."
}
```
