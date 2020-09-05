val buildNumber by extra("0")
val eventuateSpring: String by System.getProperties()
val eventuateCore: String by System.getProperties()
java.sourceCompatibility = JavaVersion.VERSION_11

extra["springCloudVersion"] = "Hoxton.SR6"
extra["springBootAdminVersion"] = "2.2.4"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("org.springframework.cloud:spring-cloud-config-client")
    implementation("org.postgresql:postgresql:42.2.10")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation(project(":common"))
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
        image = "elvaliev/account"
        tags = setOf("$version", "$version.${extra["buildNumber"]}")
        auth {
            username = System.getenv("DOCKERHUB_USERNAME")
            password = System.getenv("DOCKERHUB_PASSWORD")
        }
    }
    container {
        labels = mapOf(
            "maintainer" to "Elina Valieva <veaufa@mail.ru>",
            "org.opencontainers.image.title" to "account",
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
        workingDirectory = "/account"
    }
}
