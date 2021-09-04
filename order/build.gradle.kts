java.sourceCompatibility = JavaVersion.VERSION_11
val buildNumber by extra("0")
val eventuateSpring: String by System.getProperties()
val eventuateCore: String by System.getProperties()
extra["springCloudVersion"] = "Hoxton.SR6"
extra["springBootAdminVersion"] = "2.5.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("org.springframework.cloud:spring-cloud-config-client")
    implementation(project(":common"))
    implementation("org.postgresql:postgresql:42.2.10")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("io.eventuate.tram.sagas:eventuate-tram-sagas-orchestration-simple-dsl:$eventuateCore")
    implementation("io.eventuate.tram.sagas:eventuate-tram-sagas-spring-orchestration:$eventuateCore")
    implementation("io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:$eventuateCore")
    implementation("io.eventuate.tram.core:eventuate-tram-spring-optimistic-locking:$eventuateSpring")
    implementation("io.eventuate.tram.core:eventuate-tram-spring-consumer-jdbc:$eventuateSpring")
    implementation("io.eventuate.tram.core:eventuate-tram-spring-producer-jdbc:$eventuateSpring")
    implementation("io.eventuate.tram.core:eventuate-tram-spring-consumer-kafka:$eventuateSpring")
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

jib {
    to {
        image = "elvaliev/order"
        tags = setOf("$version", "$version.${extra["buildNumber"]}")
        auth {
            username = System.getenv("DOCKERHUB_USERNAME")
            password = System.getenv("DOCKERHUB_PASSWORD")
        }
    }
    container {
        labels = mapOf(
            "maintainer" to "Elina Valieva <veaufa@mail.ru>",
            "org.opencontainers.image.title" to "order",
            "org.opencontainers.image.description" to "Spring microservices",
            "org.opencontainers.image.version" to "$version",
            "org.opencontainers.image.authors" to "Elina Valieva <veaufa@mail.ru>>",
            "org.opencontainers.image.url" to "https://github.com/ElinaValieva/spring-microservices"
        )
        jvmFlags = listOf(
            "-server",
            "-Djava.awt.headless=true",
            "-XX:InitialRAMFraction=2",
            "-XX:MinRAMFraction=2",
            "-XX:MaxRAMFraction=2",
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=100",
            "-XX:+UseStringDeduplication"
        )
        workingDirectory = "/order"
    }
}

