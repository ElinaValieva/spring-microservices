package com.example.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
