package com.example.e2e

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class E2eApplication

fun main(args: Array<String>) {
    runApplication<E2eApplication>(*args)
}
