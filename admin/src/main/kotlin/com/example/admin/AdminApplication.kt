package com.example.admin

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableAdminServer
@SpringBootApplication
class AdminApplication

fun main(args: Array<String>) {
    runApplication<AdminApplication>(*args)
}
