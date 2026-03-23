# Platform Service API Endpoints

본 문서는 `platform-service`에서 제공하는 RESTful API 엔드포인트 목록을 정의합니다. 모든 API는 기본적으로 `/api/v1` 접두사를 가집니다.

---

## 1. 인증 및 사용자 (Auth)

인증, 소셜 로그인, 토큰 재발급 및 휴대전화 본인인증과 관련된 API입니다.

| HTTP Method | Endpoint | 설명 | Request | Response |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/auth/social/redirect` | 소셜 로그인 리다이렉트 URL 요청 | Query Params: `provider` (예: KAKAO) | `SocialAuthResult` (URL 포함) |
| `POST` | `/api/v1/auth/login` | 소셜 로그인 인증 처리 (콜백) | Body: `LoginRequest` (provider, code) | `LoginResult` (Tokens 또는 404) |
| `POST` | `/api/v1/auth/refresh` | Access Token 재발급 | Body: `RefreshTokenRequest` (refreshToken) | `RefreshTokenResult` (새 토큰) |
| `POST` | `/api/v1/auth/verify-phone` | 휴대전화 인증 문자 발송 및 코드 검증 | Body: `PhoneVerificationRequest` | `PhoneVerificationResult` |

---

## 2. 사업체 (Business)

사업체 정보 검색 및 직원 등록 요청과 관련된 API입니다.

| HTTP Method | Endpoint | 설명 | Request | Response |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/businesses/search` | 가게 이름으로 사업체 검색 (페이징 지원) | Query Params: `keyword`, `page`, `size` | `SearchBusinessResult` (Page 객체) |
| `POST` | `/api/v1/businesses/{businessId}/join-requests` | 사용자가 특정 사업체에 직원 등록을 요청 | Path Variable: `businessId`<br>Body: `JoinBusinessRequest` (userId) | `JoinBusinessResult` |

---

## 3. 초대 (Invitation)

사장님이 직원을 사업체에 초대하는 기능과 관련된 API입니다.

| HTTP Method | Endpoint | 설명 | Request | Response |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/invitations` | 전화번호를 통해 사용자를 사업체에 초대 | Body: `InviteRequest` (inviterId, branchId, targetUserPhoneNumber, roleIds) | `InviteResult` |

---

> **참고**: 상세한 DTO 구조 및 응답 코드는 애플리케이션 실행 후 `http://localhost:8080/swagger-ui.html`에서 Swagger 문서를 통해 확인할 수 있습니다.
