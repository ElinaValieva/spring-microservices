package com.example.gateway.configuration

import com.example.gateway.Delivery
import com.example.gateway.Order
import com.example.gateway.Product
import com.example.gateway.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.InetAddress

@Component
class Client(private val client: WebClient) {

    fun register(user: User): Mono<Void> {
        return client.post()
            .uri("/account/register")
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(Void::class.java)
    }

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
            .exchangeToMono { it.bodyToMono(Order::class.java) }
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
class ClientConfiguration {

    @Value("\${server.port}")
    lateinit var port: String

    @Value("\${zuul.prefix}")
    lateinit var prefixApi: String

    @Bean
    fun webClient() = WebClient.builder()
        .baseUrl("http://${InetAddress.getLoopbackAddress().hostName}:${port}/${prefixApi}/")
        .build()

}