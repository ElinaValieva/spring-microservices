java.sourceCompatibility = JavaVersion.VERSION_11


dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.testcontainers:testcontainers:1.14.3")
    testImplementation("org.testcontainers:junit-jupiter:1.14.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}