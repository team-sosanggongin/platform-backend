# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Gradle monorepo** for a platform backend with four modules:
- `_platform` — Core platform API (Spring Boot 3.2.1, Java 17)
- `_backoffice/backend` — Admin API (Spring Boot 3.2.1, Java 17, package `com.backoffice.sosangongin`)
- `_backoffice/frontend` — Admin UI (Next.js 14, TypeScript, App Router)
- `_commons` — Shared Java library (currently empty, depended on by `_platform` and `_backoffice/backend`)
- `_infra` — Infrastructure as Code (Terraform for AWS)

## Common Commands

### Build & Test
```bash
# Build all modules
./gradlew build

# Run tests for _platform
./gradlew :_platform:test

# Run tests for backoffice backend
./gradlew :_backoffice:backend:test

# Run a single test class
./gradlew :_platform:test --tests com.platform.sosangongin.services.jwt.JwtServiceTest

# Run the platform application
./gradlew :_platform:bootRun

# Run backoffice backend
./gradlew :_backoffice:backend:bootRun
```

### Infrastructure (Terraform via Gradle)
```bash
./gradlew tfInit       # terraform init
./gradlew tfPlanStg    # terraform plan (staging)
./gradlew tfApplyStg   # terraform apply (staging)
./gradlew tfDestroyStg # terraform destroy (staging)
```

### Frontend (backoffice)
```bash
cd _backoffice/frontend
npm install
npm run dev    # Next.js dev server
npm run lint   # ESLint
```

## Architecture

### `_platform` Layer Structure

```
api/
  controllers/   ← REST controllers (Auth, Business, Invitation, Notice, App)
  dto/           ← Request/response DTOs for controllers
  security/      ← JwtAuthenticationFilter (extracts JWT from requests)
  resolver/      ← @LoginUser annotation + LoginUserArgumentResolver
  interceptor/   ← MetricsInterceptor
  advices/       ← Controller advice (exception handling)
cases/           ← Use case orchestrators (e.g., LoginUsecase, PhoneVerificationUsecase)
services/        ← External integrations (JWT, OAuth/Kakao, SMS/FCM push, external HTTP)
domains/         ← JPA entities + Spring Data repositories
config/          ← Spring configuration (Security, Swagger, WebClient, Firebase, JPA auditing)
errors/          ← Custom exceptions
```

The flow is: **Controller** → **Use Case** → **Service/Repository** → **Domain Entity**. Controllers handle HTTP concerns; use cases coordinate one business flow end-to-end; services handle infrastructure concerns.

Authenticated endpoints use `@LoginUser` annotation on controller method parameters, resolved by `LoginUserArgumentResolver` which extracts the user from the JWT-authenticated security context.

### `_backoffice/backend` Structure

```
controller/    ← AuthController, NoticeController
cases/auth/    ← LoginUsecase (admin login with login history tracking)
domains/       ← AccountBackoffice, AdminLoginHistory entities + repos
gateways/      ← PlatformNoticeGateway interface + PlatformNoticeGatewayImpl (calls _platform API)
dto/           ← Notice request/response DTOs
config/        ← SecurityConfig, JpaAuditingConfig
errors/        ← GlobalExceptionHandler
```

The backoffice backend proxies notice CRUD to the platform API via the gateway pattern.

### `_backoffice/frontend` Structure

Next.js App Router with route groups:
- `(auth)/` — Login, verify, locked, change-password pages
- `(main)/backoffice/` — Dashboard, notices, roles, users management

Component hierarchy: `atoms/` (Button, Input, Badge, Card, Select) → `molecules/` (ConfirmModal, DetailRow) → `layout/` (Header, Footer, Container, ListLayout)

### Key Domain Model

```
User (phone-number unique)
├── PhoneVerification    ← SMS-based OTP for account activation
├── UserSocialAuth       ← Kakao OAuth mappings
└── RefreshToken         ← Persisted JWT refresh tokens

Business
├── Employment           ← User ↔ Business membership
│   └── EmploymentRole
└── BusinessRole
    └── RolePermission   ← Fine-grained ACL

Invitation              ← Inviting users to businesses
PublicNotice            ← Announcements
```

### Auth Strategy

- **Dual-token JWT**: `accessToken` (15 min) + `refreshToken` (14 days)
- `JwtAuthenticationFilter` in the Spring Security filter chain validates tokens
- UUID-based primary keys on all entities
- Soft delete via `SoftDeletedBaseEntity`
- Phone verification required before account activation
- Kakao OAuth supported (`KakaoOauthProperties`)
- Backoffice has separate auth with `AccountBackoffice` entity and login history tracking

### Spring Profiles

| Profile | Database       |
|---------|---------------|
| local   | H2 in-memory  |
| test    | H2 (create-drop) |
| stage   | RDS (AWS)     |
| prod    | RDS (AWS)     |

Default active profile is `local`. SMS is mocked locally via `MockSmsPushService`.

### API / Observability

- Swagger UI: `/swagger-ui.html`, API docs: `/api-docs`
- Actuator: `/actuator/health`, `/actuator/prometheus`, `/actuator/metrics`

### AWS Infrastructure (`_infra/terraform/`)

- **ECS Fargate** containers in public subnets behind **API Gateway** (VPC Link)
- **RDS** in private subnets, only reachable from ECS security groups
- Multi-AZ: `ap-northeast-2a` and `ap-northeast-2c`
- Environments: `env/local.tfvars`, `env/stg.tfvars`, `env/prod.tfvars`
- Staging VPC: `10.21.0.0/16` / Prod VPC: `10.22.0.0/16`
- Docker image uses `eclipse-temurin:21-jre` base

## Key Conventions

- Package naming: `com.platform.sosangongin` for `_platform`, `com.backoffice.sosangongin` for backoffice backend
- JWT config is in `application.yml` under `jwt.secret-key`, `jwt.expiration-time`, `jwt.refresh-token-expiration-time` (env-var overridable)
- Tests use JUnit 5 (`useJUnitPlatform()`) with `spring-boot-starter-test` and `spring-security-test`
- Lombok is used project-wide for boilerplate reduction

## Domain Specs

- [_platform/usecase.md](_platform/usecase.md) — User registration, token lifecycle
- [_platform/cases.md](_platform/cases.md) — Entity relationships, business rules
- [_platform/api-endpoints.md](_platform/api-endpoints.md) — API endpoint reference
- [_platform/rest-api-structure.md](_platform/rest-api-structure.md) — REST API structure
- [_infra/aws-network-architecture.md](_infra/aws-network-architecture.md) — Network design
