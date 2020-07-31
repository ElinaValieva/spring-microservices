package com.example.gateway

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.InetAddress

@Component
class Client(private val client: WebClient) {

    fun getUserById(userId: String): Mono<User> {
        return client
            .get()
            .uri("/account/user/{id}", userId)
            .retrieve()
            .bodyToMono(User::class.java)
    }

    fun getProductById(id: String): Mono<Product> {
        return client
            .get()
            .uri("/store/{id}", id)
            .retrieve()
            .bodyToMono(Product::class.java)
    }

    fun getOrderById(id: Long): Mono<Order> {
        return client
            .get()
            .uri("/order/order/{id}", id)
            .exchange()
            .flatMap { it.bodyToMono(Order::class.java) }
    }

    fun getDeliveryInfo(id: Long): Mono<Delivery> {
        return client
            .get()
            .uri("/delivery/order/{id}", id)
            .retrieve()
            .bodyToMono(Delivery::class.java)
    }
}

@Configuration
class ClientConfiguration(private val environment: Environment) {

    @Value("\${server.port}")
    lateinit var port: String

    @Value("\${zuul.prefix}")
    lateinit var prefixApi: String

    @Bean
    fun webClient() = WebClient.builder()
        .baseUrl("http://${InetAddress.getLoopbackAddress().hostName}:${port}/${prefixApi}/")
        .build()

}