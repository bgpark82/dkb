plugins {
    /**
     * System.getProperties
     * - 자바에서 시스템 속성을 가져오는 메서드
     * - JVM이 시작할 때 설정된 시스템 속성 반환
     * - 시스템 정보, 환경 변수
     */
    val kotlinVersion: String by System.getProperties()
    val springBootVersion: String by System.getProperties()

    println("코틀린 버전 : $kotlinVersion")
    kotlin("jvm") version kotlinVersion // 코틀린 컴파일러 버전
    kotlin("plugin.spring") version kotlinVersion //
    kotlin("plugin.jpa") version kotlinVersion

    println("스프링부트 버전 : $springBootVersion")
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.bgpark.demo"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        val javaVersion: String by System.getProperties()
        println("자바 버전 : $javaVersion")
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1") // JUnit5 기반 실행기
    testImplementation("io.kotest:kotest-assertions-core:5.8.1") // 기본 assertion
    testImplementation("io.kotest:kotest-framework-engine:5.8.1") // Kotest 내부 엔진
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
