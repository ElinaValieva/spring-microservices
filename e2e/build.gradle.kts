java.sourceCompatibility = JavaVersion.VERSION_11
val testContainerVersion = "1.14.3"


dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.testcontainers:testcontainers:$testContainerVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainerVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}