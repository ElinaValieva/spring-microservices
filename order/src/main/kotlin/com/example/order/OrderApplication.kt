package com.example.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
//@EnableDiscoveryClient
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
