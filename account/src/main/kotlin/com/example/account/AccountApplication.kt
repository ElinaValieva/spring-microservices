package com.example.account

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class AccountApplication

fun main(args: Array<String>) {
    runApplication<AccountApplication>(*args)
}
