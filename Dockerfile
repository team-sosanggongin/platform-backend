# ---------------------------------------------------------
# 🏭 1단계: 빌드 스테이지 (Build Stage) - "빌드 공장"
# ---------------------------------------------------------
# JDK 21이 설치된 경량 Alpine 리눅스 이미지를 사용합니다.
# 빌드 도구들이 포함되어 있어 무겁지만, 마지막 이미지에는 남지 않습니다.
FROM eclipse-temurin:21-jre
WORKDIR /app

# [최적화 핵심] Gradle 래퍼와 의존성 설정 파일만 먼저 복사합니다.
# 소스코드가 바뀌어도 라이브러리가 안 바뀌었다면, 이 단계는 캐시를 재사용하여
# 매번 라이브러리를 다시 다운로드하는 시간을 획기적으로 줄입니다.
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY buildSrc buildSrc
# Gradle 래퍼에 실행 권한을 부여하고, 의존성만 먼저 다운로드합니다.
# (이때 소스코드가 없으므로 실제 빌드는 실패하지만 의존성은 캐싱됩니다.)
RUN ./gradlew dependencies --no-daemon || return 0