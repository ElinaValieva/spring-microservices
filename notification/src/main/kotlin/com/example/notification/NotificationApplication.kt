package com.example.notification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class NotificationApplication

fun main(args: Array<String>) {
    runApplication<NotificationApplication>(*args)
}
