plugins {
    /**
     * System.getProperties
     * - 자바에서 시스템 속성을 가져오는 메서드
     * - JVM이 시작할 때 설정된 시스템 속성 반환
     * - 시스템 정보, 환경 변수
     */
    val kotlinVersion: String by System.getProperties()
    val springBootVersion: String by System.getProperties()
    val serenityVersion: String by System.getProperties()

    println("코틀린 버전 : $kotlinVersion")
    kotlin("jvm") version kotlinVersion // 코틀린 컴파일러 버전
    kotlin("plugin.spring") version kotlinVersion //
    kotlin("plugin.jpa") version kotlinVersion

    println("스프링부트 버전 : $springBootVersion")
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.7"

    id("net.serenity-bdd.serenity-gradle-plugin") version serenityVersion

//    id("net.serenity-bdd.serenity-gradle-plugin") version "4.1.2" // Serenity BDD Gradle Plugin

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
    val serenityVersion: String by System.getProperties()
    val cucumberVersion: String by System.getProperties()

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

    // Cucumber Dependencies
    testImplementation("io.cucumber:cucumber-java:${cucumberVersion}")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:${cucumberVersion}") // For JUnit Platform runner, serenity 사용 시, serentiy가 자체 Runner를 사용한다
    testImplementation("io.cucumber:cucumber-spring:${cucumberVersion}") // For Spring integration in Cucumber

    // RestAssured for API Testing
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("io.rest-assured:json-path:5.4.0") // For easily parsing JSON responses
    testRuntimeOnly("org.junit.platform:junit-platform-suite") // For Cucumber runner

    // Serenity BDD with Cucumber and JUnit 5
    testImplementation("net.serenity-bdd:serenity-core:${serenityVersion}") // Serenity BDD 코어 (요청하신 4.2.30)
    testImplementation("net.serenity-bdd:serenity-junit5:${serenityVersion}")  // JUnit 5 통합
    testImplementation("net.serenity-bdd:serenity-rest-assured:${serenityVersion}") // RestAssured 통합
    testImplementation("net.serenity-bdd:serenity-cucumber:${serenityVersion}")  // Cucumber 통합
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

serenity {
    outputDirectory = "build/serenity-reports"
//    takeScreenshots = "DISABLED"     // 스크린샷 캡처 정책 (API 테스트이므로 DISABLED)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    // 테스트 완료 후 Serenity 보고서 집계 작업 실행
//    finalizedBy(tasks.serenityAggregate)
    systemProperty("cucumber.object-factory", "io.cucumber.spring.SpringFactory")
}