package com.example.store

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
//@EnableDiscoveryClient
class StoreApplication

fun main(args: Array<String>) {
    runApplication<StoreApplication>(*args)
}
