package com.example.order

import com.example.order.repository.OrderRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class])
@EnableDiscoveryClient
@EnableMongoRepositories(basePackageClasses = [OrderRepository::class])
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
