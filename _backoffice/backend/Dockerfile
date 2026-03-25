# ---------------------------------------------------------
# 🏭 1단계: 빌드 스테이지 (Build Stage) - "빌드 공장"
# ---------------------------------------------------------
# JDK 21이 설치된 경량 Alpine 리눅스 이미지를 사용합니다.
# 빌드 도구들이 포함되어 있어 무겁지만, 마지막 이미지에는 남지 않습니다.
FROM eclipse-temurin:21-jdk-alpine AS builder

# 공장 안의 작업 디렉토리를 설정합니다.
WORKDIR /app

# [최적화 핵심] Gradle 래퍼와 의존성 설정 파일만 먼저 복사합니다.
# 소스코드가 바뀌어도 라이브러리가 안 바뀌었다면, 이 단계는 캐시를 재사용하여
# 매번 라이브러리를 다시 다운로드하는 시간을 획기적으로 줄입니다.
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY buildSrc buildSrc # 멀티 모듈인 경우 필수

# Gradle 래퍼에 실행 권한을 부여하고, 의존성만 먼저 다운로드합니다.
# (이때 소스코드가 없으므로 실제 빌드는 실패하지만 의존성은 캐싱됩니다.)
RUN ./gradlew dependencies --no-daemon || return 0

# 이제 실제 소스코드를 복사합니다.
COPY . .

# [최적화] 실행 환경에 맞는 실제 bootJar만 빌드합니다.
# 예를 들어 사용자용 API 서버만 빌드한다면 아래와 같이 특정 모듈만 지정합니다.
# --no-daemon 옵션은 도커 빌드 환경에서 불필요한 데몬 프로세스를 띄우지 않아 가볍습니다.
RUN ./gradlew :backoffice:bootJar --no-daemon


# ---------------------------------------------------------
# 🏃‍♂️ 2단계: 실행 스테이지 (Run Stage) - "실행 전용 방"
# ---------------------------------------------------------
# JDK 대신 실행에만 필요한 JRE(Java Runtime Environment)만 포함된
# 더 가벼운 Alpine 이미지를 사용합니다. 빌드 도구가 없어 매우 작고 안전합니다.
FROM eclipse-temurin:21-jre-alpine

# 실행 방의 작업 디렉토리를 설정합니다.
WORKDIR /app

# 보안을 위해 root 사용자가 아닌 별도의 시스템 사용자를 생성하여 실행합니다.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# [최적화 핵심] 빌드 스테이지 공장에서 완성된 jar 파일만 쏙 가져옵니다.
# 나머지 소스코드, Gradle 래퍼, 캐시 파일 등은 모두 버려집니다.
COPY --from=builder /app/backoffice/build/libs/*.jar app.jar

# Spring Boot의 기본 포트인 8080을 외부로 노출합니다.
EXPOSE 8080

# [최적화] 애플리케이션 실행 명령
# -Djava.security.egd=file:/dev/./urandom: Alpine 리눅스에서 난수 생성 속도를 높여 부팅을 빨라지게 합니다.
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]