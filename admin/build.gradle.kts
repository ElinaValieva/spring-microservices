java.sourceCompatibility = JavaVersion.VERSION_11
val buildNumber by extra("0")

extra["springBootAdminVersion"] = "2.2.4"
extra["springCloudVersion"] = "Hoxton.SR6"


dependencies {
    implementation("de.codecentric:spring-boot-admin-starter-server")
    implementation("de.codecentric:spring-boot-admin-server-ui-login:1.5.7")
    implementation("org.springframework.cloud:spring-cloud-config-client")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

dependencyManagement {
    imports {
        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

jib {
    to {
        image = "elvaliev/admin"
        tags = setOf("$version", "$version.${extra["buildNumber"]}")
        auth {
            username = System.getenv("DOCKERHUB_USERNAME")
            password = System.getenv("DOCKERHUB_PASSWORD")
        }
    }
    container {
        labels = mapOf(
            "maintainer" to "Elina Valieva <veaufa@mail.ru>",
            "org.opencontainers.image.title" to "admin",
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
        workingDirectory = "/admin"
        ports = listOf("8888")
    }
}
