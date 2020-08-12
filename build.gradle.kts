import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by System.getProperties()
val swaggerVersion: String by System.getProperties()

plugins {
    id("org.springframework.boot") version "2.3.2.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.9.RELEASE" apply false
    id("com.google.cloud.tools.jib") version "2.4.0" apply false
    kotlin("jvm") version "1.3.72" apply false
    kotlin("plugin.spring") version "1.3.72" apply false
}

subprojects {
    if (this.name == "cqrs_command") {
        apply {
            plugin("org.jetbrains.kotlin.jvm")
            plugin("org.jetbrains.kotlin.plugin.spring")
        }
    } else {
        apply {
            plugin("io.spring.dependency-management")
            plugin("org.springframework.boot")
            plugin("org.jetbrains.kotlin.jvm")
            plugin("org.jetbrains.kotlin.plugin.spring")
            plugin("com.google.cloud.tools.jib")
        }
    }

    group = "org.example"
    version = "1.0.0"
    val implementation by configurations

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("io.springfox:springfox-swagger2:$swaggerVersion")
        implementation("io.springfox:springfox-swagger-ui:$swaggerVersion")
    }

    repositories {
        mavenCentral()
        jcenter()
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

}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.create("helm") {
    group = "deploy"
    description = "Helm Chart installation"
    doLast {
        val names = mutableMapOf<String, String>()
        fileTree("helm").visit {
            if (this.isDirectory && this.name !in listOf("charts", "templates"))
                names[this.name] = this.name
                    .replace("[.,;:_-]?".toRegex(), "")
                    .replace("chart", "")
        }
        names.forEach { (key, value) ->
            exec {
                if (Os.isFamily(Os.FAMILY_WINDOWS))
                    commandLine("cmd", "/c", "helm install $value $key")
                else
                    commandLine("sh", "-c", "helm install $value $key")
            }
        }
    }
}
