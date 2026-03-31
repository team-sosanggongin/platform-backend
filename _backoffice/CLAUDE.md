# Backoffice CLAUDE.md

백오피스(backend + frontend) 전용 가이드. 루트 CLAUDE.md의 프로젝트 전체 구조와 함께 참조.

## Backend 아키텍처 현황

### 레이어 구조

```
controller/          ← REST 엔드포인트, 세션 처리, API DTO ↔ 내부 DTO 변환
controller/dto/      ← API 요청/응답 전용 DTO (XxxRequest, XxxResponse)
cases/               ← UseCase (비즈니스 흐름 조율)
cases/xxx/           ← 내부 DTO (XxxCommand 등, UseCase 입출력)
domains/             ← JPA 엔티티 + Repository + 값 객체
config/              ← Spring 설정 (Security, Session, CORS)
errors/              ← 커스텀 예외 계층
```

흐름: **Controller** → **UseCase** → **Repository** → **Entity**

### 패키지 상세

```
controller/
├── AuthController.java           ← /api/auth/** (login, me, change-password, logout)
├── NoticeController.java         ← /api/notices/** (CRUD)
└── dto/
    ├── LoginResponse.java        ← 로그인/내정보 응답
    └── notice/
        ├── CreateNoticeRequest.java
        ├── UpdateNoticeRequest.java
        └── NoticeResponse.java

cases/
├── auth/
│   ├── LoginUsecase.java         ← 로그인 (실패 시 커스텀 예외 throw, 성공 시 BackofficeAdmin 반환)
│   ├── ChangePasswordUsecase.java ← 비밀번호 변경 (Result 패턴, 추후 예외 전환 대상)
│   ├── GetMyInfoUsecase.java     ← 내 정보 조회
│   ├── LoginRequest.java
│   ├── ChangePasswordRequest.java
│   └── ChangePasswordResult.java
└── notice/
    ├── NoticeUsecase.java        ← 공지 CRUD
    ├── CreateNoticeCommand.java  ← 내부 DTO (status: NoticeStatus enum)
    └── UpdateNoticeCommand.java

domains/
├── account/
│   ├── BackofficeAdmin.java      ← 관리자 엔티티 (UUID PK)
│   └── BackofficeAdminRepository.java
├── notice/
│   ├── BackofficeNotice.java     ← 공지 엔티티 (@Table("public_notice"), Long PK)
│   ├── BackofficeNoticeRepository.java
│   ├── NoticeStatus.java         ← DRAFT, PUBLISHED, SCHEDULED, HIDDEN
│   └── NoticeContent.java        ← 값 객체 (update/create 파라미터 캡슐화)
├── loginHistory/
│   ├── AdminLoginHistory.java
│   ├── AdminLoginHistoryRepository.java
│   └── AdminLoginHistoryService.java  ← REQUIRES_NEW 트랜잭션
└── common/
    ├── BaseEntity.java           ← createdAt, updatedAt (JPA Auditing)
    └── SoftDeletedBaseEntity.java ← deletedAt

config/
├── SecurityConfig.java           ← 필터체인, CORS 연결, 프로필 분기, 401 엔트리포인트
├── SessionManager.java           ← 세션 키 "ACCOUNT_ID" 중앙 관리
├── SessionAuthenticationFilter.java ← 세션 → SecurityContext 연결
├── CorsConfig.java               ← CORS 설정 (cors.allowed-origins 프로퍼티)
└── JpaAuditingConfig.java

errors/
├── BackofficeBusinessError.java  ← 공통 베이스 (RuntimeException)
├── EntityNotFoundException.java  ← 404
├── InvalidCredentialsException.java ← 401
├── AccountLockedException.java   ← 403
├── GlobalExceptionHandler.java   ← 예외 → HTTP 상태 매핑
└── ErrorResponse.java
```

### Auth API

| 엔드포인트 | 메서드 | 용도 | 인증 |
|-----------|--------|------|------|
| `/api/auth/login` | POST | 로그인 | 불필요 |
| `/api/auth/me` | GET | 현재 세션 사용자 조회 | 불필요 (세션 직접 확인) |
| `/api/auth/change-password` | POST | 비밀번호 변경 | 세션 필요 |
| `/api/auth/logout` | POST | 로그아웃 | 세션 필요 |

### Notice API

| 엔드포인트 | 메서드 | 용도 |
|-----------|--------|------|
| `/api/notices` | POST | 공지 생성 |
| `/api/notices/{id}` | GET | 공지 단건 조회 |
| `/api/notices` | GET | 공지 목록 조회 |
| `/api/notices/{id}` | PUT | 공지 수정 |
| `/api/notices/{id}` | DELETE | 공지 삭제 (soft delete) |

### 인증 구조

- 세션 기반 (JWT 아님), 타임아웃 60분
- `SessionManager`가 세션 키 "ACCOUNT_ID"를 중앙 관리
- `SessionAuthenticationFilter`가 세션 → SecurityContext 연결
- local 프로필: 인증 오픈 (anyRequest().permitAll())
- 그 외: /api/auth/** 만 permitAll, 나머지 authenticated

### 예외 처리 전략

- JDK 기본 예외(IllegalArgumentException 등) 사용 금지
- `BackofficeBusinessError` 상속 커스텀 예외만 사용
- `GlobalExceptionHandler`에서 예외 타입별 HTTP 상태코드 매핑
- LoginUsecase: 커스텀 예외 전환 완료
- ChangePasswordUsecase: Result 패턴 잔존 (관련 코드 수정 시 함께 전환)

### Notice 도메인 설계

- `BackofficeNotice`가 `@Table("public_notice")` 매핑 — platform의 `PublicNotice`와 같은 테이블
- 백오피스가 CRUD 주인, platform은 조회만
- `NoticeContent` 값 객체로 create/update 파라미터 캡슐화
- `publishedAt` 자동 세팅: 엔티티 내부에서 처리 (status가 PUBLISHED일 때)
- status 응답은 소문자 (프론트 호환): `notice.getStatus().name().toLowerCase()`

### DTO 변환 흐름

```
Request(String status) → toCommand()에서 valueOf() → Command(NoticeStatus enum)
Command → toNoticeContent() → NoticeContent(값 객체)
NoticeContent → BackofficeNotice.create() / update()
Entity → NoticeResponse.from(entity) → JSON 응답
```

### Spring Profiles

| Profile | DB | 인증 | 비고 |
|---------|-----|------|------|
| local (기본) | H2 인메모리 | 오픈 | h2-console 활성화 |
| test | H2 인메모리 | - | create-drop |
| stg | RDS (skeleton) | 세션 필터 적용 | ddl-auto: validate |
| prod | RDS (skeleton) | 세션 필터 적용 | ddl-auto: validate |

## Frontend 현황

- Next.js 14, TypeScript, App Router
- 전부 mock 데이터 (API 연동 없음)
- 스타일: 인라인 스타일 100%
- 컴포넌트: Atomic Design (atoms/molecules/layout)
- 라우팅: (auth) 그룹 + (main) 그룹

### 페이지 목록

| 경로 | 기능 | API 연동 |
|------|------|---------|
| `/login` | 로그인 | 미연동 |
| `/login/change-password` | 비밀번호 변경 | 미연동 |
| `/verify` | SMS 인증 | 미연동 (보류) |
| `/locked` | 계정 잠금 표시 | 미연동 |
| `/backoffice` | 대시보드 | 미연동 |
| `/backoffice/users` | 사용자 목록 | 미연동 |
| `/backoffice/users/new` | 사용자 생성 | 미연동 |
| `/backoffice/users/[id]` | 사용자 상세 | 미연동 |
| `/backoffice/roles` | 역할 관리 | 미연동 |
| `/backoffice/notices` | 공지사항 관리 | 미연동 |

### 프론트 ↔ 백엔드 필드 매핑 (Notice)

| 프론트 필드 | 백엔드 응답 필드 | 비고 |
|------------|----------------|------|
| author | author | 엔티티는 authorName, 응답 시 변환 |
| startAt | startAt | 엔티티는 startsAt, 응답 시 변환 |
| endAt | endAt | 엔티티는 endsAt, 응답 시 변환 |
| status | status (소문자) | published, draft, scheduled |

## 남은 작업

### 프론트 연동 (백엔드 준비 완료)
- Auth 페이지 연동 (login, change-password, logout)
- Notice 페이지 연동
- API 클라이언트 설정 (base URL, credentials: include)

### 백엔드 미구현
- User 관리 (도메인 새로 필요)
- Role/Permission 관리 (도메인 새로 필요)
- Dashboard 통계 API
- SMS 본인인증 (보류, mock 대체)