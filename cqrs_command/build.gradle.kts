java.sourceCompatibility = JavaVersion.VERSION_11
val eventuateSpring: String by System.getProperties()

dependencies {
    implementation("io.eventuate.tram.core:eventuate-tram-spring-commands:$eventuateSpring")
}