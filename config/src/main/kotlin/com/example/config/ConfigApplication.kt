package com.example.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigServer
class ConfigApplication

fun main(args: Array<String>) {
    runApplication<ConfigApplication>(*args)
}
