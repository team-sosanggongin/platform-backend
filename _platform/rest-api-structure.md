# Platform Service REST API Architecture & Endpoints

본 문서는 `platform-service`의 REST API 구조, 공통 정책, 인증 방식 및 엔드포인트 목록을 종합적으로 분석한 문서입니다.

---

## 1. 아키텍처 및 공통 정책 (Architecture & Policies)

### 1.1. 계층 분리 (Layer Separation)
*   **Web DTO 도입**: 컨트롤러(Web Layer)와 비즈니스 로직(UseCase Layer)의 결합도를 낮추기 위해 전용 API DTO를 사용합니다.
    *   요청: `XXXApiRequest` (예: `LoginApiRequest`)
    *   응답: `XXXApiResponse` (예: `LoginApiResponse`)
*   컨트롤러는 `ApiRequest`를 받아 `toUseCaseRequest()`를 통해 도메인 객체로 변환하여 UseCase로 전달하고, 반환된 Result를 `ApiResponse`로 감싸서 클라이언트에 전달합니다.

### 1.2. 공통 응답 처리 (Response Advice)
*   모든 API 응답 DTO는 `CommonResultTemplate`을 상속받습니다. (필드: `httpStatus`, `message`)
*   **`CommonResultResponseAdvice`**: 컨트롤러가 반환하는 객체를 가로채어, 객체 내부에 명시된 `httpStatus` 값을 실제 HTTP Response Header의 Status Code로 자동 변환해 줍니다.
*   이를 통해 컨트롤러 내부에서 중복되는 `ResponseEntity.status(...).body(...)` 코드를 제거했습니다.

### 1.3. 메트릭 수집 (Metrics Interceptor)
*   **`MetricsInterceptor`**: 모든 API 요청 처리 완료 후 응답 코드를 분석하여 200번대, 400번대, 500번대 카운터를 증가시킵니다.
*   이를 통해 Prometheus에서 에러 비율을 실시간으로 계산하고 모니터링할 수 있습니다. (`app.response.status.total` 메트릭)

---

## 2. 인증 및 인가 (Authentication & Authorization)

우리 시스템은 **Spring Security + JWT (JSON Web Token)** 기반의 Stateless 인증 방식을 사용합니다.

### 2.1. 인증 처리 상세 흐름 (Authentication Flow)

클라이언트의 요청이 컨트롤러에 도달하기까지 다음과 같은 파이프라인을 거치며 인증 정보가 추출 및 주입됩니다.

1.  **클라이언트 요청**: 클라이언트가 HTTP Header에 토큰을 담아 API를 호출합니다.
    ```http
    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5c...
    ```
2.  **Spring Security 필터 체인 진입**: 요청이 `SecurityConfig`에 정의된 필터 체인에 진입합니다. (`/api/v1/auth/**` 등 공개 경로는 인증 로직을 건너뜁니다.)
3.  **`JwtAuthenticationFilter` 실행**:
    *   `Authorization` 헤더에서 `Bearer ` 접두사를 제거하고 순수 JWT 문자열을 추출합니다.
    *   `JwtService.getUserIdFromToken(token)`을 호출하여 토큰의 서명을 검증하고 payload에서 `userId`를 파싱합니다.
4.  **SecurityContext 저장**:
    *   파싱된 `userId`를 `Principal`로 삼아 `UsernamePasswordAuthenticationToken` 객체를 생성합니다.
    *   이 인증 객체를 Spring Security의 전역 저장소인 **`SecurityContextHolder`**에 저장합니다. (이제 시스템은 해당 요청을 '인증된 사용자'의 요청으로 인식합니다.)
5.  **컨트롤러 도달 전 Argument Resolver 개입**:
    *   요청이 `DispatcherServlet`을 거쳐 타겟 컨트롤러의 메서드(예: `requestToJoin`)로 매핑됩니다.
    *   메서드 파라미터 중 **`@LoginUser`** 어노테이션이 붙은 파라미터를 발견하면, 스프링은 **`LoginUserArgumentResolver`**를 실행합니다.
6.  **유저 정보 주입**:
    *   `LoginUserArgumentResolver`는 `SecurityContextHolder`에서 현재 쓰레드의 `Authentication` 객체를 꺼내옵니다.
    *   `Authentication.getPrincipal()`에 저장되어 있던 `userId`를 추출하여 컨트롤러 메서드의 파라미터로 넘겨줍니다.
7.  **컨트롤러 실행**:
    *   컨트롤러는 HTTP Request나 Header를 직접 까볼 필요 없이, 이미 검증되어 타입 캐스팅까지 완료된 `userId`를 비즈니스 로직에 즉시 사용합니다.

---

## 3. API 엔드포인트 목록 (Endpoints)

모든 API는 기본적으로 `/api/v1` 접두사를 가집니다.

### 3.1. 인증 및 사용자 (Auth) - `AuthController`
| HTTP Method | Endpoint | 인증 필요 | 설명 | Web Request DTO | Web Response DTO |
| :--- | :--- | :---: | :--- | :--- | :--- |
| `GET` | `/auth/social/redirect` | ❌ | 소셜 로그인(Kakao 등) 창으로 리다이렉트할 URL 반환 | `SocialAuthApiRequest` | `SocialAuthApiResponse` |
| `POST` | `/auth/login` | ❌ | 소셜 인증 코드를 받아 서비스 로그인 (또는 회원가입 유도) | `LoginApiRequest` | `LoginApiResponse` |
| `POST` | `/auth/refresh` | ❌ | 만료된 Access Token을 Refresh Token으로 재발급 | `RefreshTokenApiRequest` | `RefreshTokenApiResponse` |
| `POST` | `/auth/verify-phone` | ❌ | 휴대전화 인증 요청 및 인증 번호 검증 | `PhoneVerificationApiRequest` | `PhoneVerificationApiResponse` |

### 3.2. 사업체 (Business) - `BusinessController`
| HTTP Method | Endpoint | 인증 필요 | 설명 | Web Request DTO | Web Response DTO |
| :--- | :--- | :---: | :--- | :--- | :--- |
| `GET` | `/businesses/search` | ⭕️ | 가게 이름(키워드)으로 사업체 목록 검색 (페이징, 메타데이터 포함) | Query Params (`keyword`, `page`, `size`) | `SearchBusinessApiResponse` |
| `POST` | `/businesses/{businessId}/join-requests` | ⭕️ | **직원 -> 사장님**: 특정 사업체에 직원 등록(합류) 요청 | `JoinBusinessApiRequest` (Body 생략 가능) | `JoinBusinessApiResponse` |

### 3.3. 초대 (Invitation) - `InvitationController`
| HTTP Method | Endpoint | 인증 필요 | 설명 | Web Request DTO | Web Response DTO |
| :--- | :--- | :---: | :--- | :--- | :--- |
| `POST` | `/invitations` | ⭕️ | **사장님 -> 직원**: 전화번호로 특정 사용자를 사업체에 초대 (SMS 발송) | `InviteApiRequest` | `InviteApiResponse` |

---

> **참고**: 더 상세한 Request Body 필드와 Response Schema 구조는 로컬 서버 실행 후 `http://localhost:8080/swagger-ui.html`에 접속하여 확인할 수 있습니다.
