package com.example.admin

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.context.config.annotation.RefreshScope

@EnableAdminServer
@EnableDiscoveryClient
@SpringBootApplication
@RefreshScope
class AdminApplication

fun main(args: Array<String>) {
    runApplication<AdminApplication>(*args)
}
