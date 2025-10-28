# -------------------------------------------------------------------------------------
# Stage 1: Build Stage (Gradle을 사용하여 애플리케이션 빌드)
# -------------------------------------------------------------------------------------
# Gradle이 설치된 환경을 사용합니다. JDK 17을 사용합니다.
FROM gradle:8.5.0-jdk17-alpine AS build

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# Gradle Wrapper 관련 파일을 복사하여 초기 빌드 속도를 높입니다.
COPY gradlew .
COPY gradle gradle

# build.gradle 파일을 복사합니다.
COPY build.gradle settings.gradle /app/

# 의존성 파일을 다운로드하여 캐시합니다. (build.gradle 변경 시에만 실행)
RUN gradle dependencies

# 전체 소스 코드를 복사합니다.
COPY src src

# 애플리케이션을 빌드합니다.
# Spring Boot의 bootJar 태스크를 사용하여 실행 가능한 JAR 파일을 생성합니다.
RUN gradle bootJar


# -------------------------------------------------------------------------------------
# Stage 2: Runtime Stage (경량화된 실행 환경)
# -------------------------------------------------------------------------------------
# 가벼운 JRE 환경을 사용하여 최종 이미지 크기를 줄입니다. JDK 17을 사용합니다.
FROM eclipse-temurin:17-jre

# 빌드 스테이지에서 생성된 JAR 파일을 복사합니다.
# Spring Boot 3.2.0 기준, 실행 가능한 JAR 파일은 build/libs/에 생성됩니다.
# 파일명은 ${project.name}-${version}.jar 입니다.
# 여기서 project.name은 보통 디렉토리 이름이나 settings.gradle에 의해 결정되지만,
# 명시적으로 'app.jar'로 복사하여 사용합니다.
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=build ${JAR_FILE} app.jar

# Spring Boot 애플리케이션이 실행될 포트를 노출합니다. (일반적으로 8080)
EXPOSE 8080

# 컨테이너가 시작될 때 실행할 명령을 정의합니다.
# 이 명령어는 Spring Boot 애플리케이션을 실행합니다.
# `-Djava.security.egd=file:/dev/./urandom`은 엔트로피 부족 문제를 해결하여 시작 시간을 단축합니다.
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]