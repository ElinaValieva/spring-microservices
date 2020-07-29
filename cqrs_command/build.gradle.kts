import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    `maven-publish`
    maven
}

group = "com.example"
version = "1.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.eventuate.tram.core:eventuate-tram-spring-commands:0.24.0.RELEASE")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
