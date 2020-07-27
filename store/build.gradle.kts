import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
}

group = "com.example"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

extra["springCloudVersion"] = "Hoxton.SR6"
extra["springBootAdminVersion"] = "2.2.4"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.2.10")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.eventuate.tram.sagas:eventuate-tram-sagas-orchestration:0.13.0.RELEASE")
    implementation("io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.13.0.RELEASE")
    implementation("io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.13.0.RELEASE")
    implementation("io.eventuate.tram.core:eventuate-tram-spring-optimistic-locking:0.24.0.RELEASE")
    implementation("io.eventuate.tram.sagas:eventuate-tram-sagas-orchestration-simple-dsl:0.13.0.RELEASE")
    implementation("io.eventuate.tram.core:eventuate-tram-consumer-kafka:0.13.0.RELEASE")
    implementation("com.example:cqrs_command:1.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
    }
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
