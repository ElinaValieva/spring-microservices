package com.example.gateway

import com.example.gateway.API.ACCOUNT_URL
import com.example.gateway.API.ORDER_URL
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

object API {
    const val ACCOUNT_URL = "http://localhost:8885/"
    const val ORDER_URL = "http://localhost:8886/"
}

@Component
class AccountClient(
    private val client: WebClient = WebClient.builder()
        .baseUrl(ACCOUNT_URL)
        .build()
) {

    fun getUserById(userId: String): Mono<User> {
        return client
            .get()
            .uri("/hello")
            .retrieve()
            .bodyToMono(User::class.java)
    }
}

@Component
class StoreClient(
    private val client: WebClient = WebClient.builder()
        .baseUrl(ACCOUNT_URL)
        .build()
) {

    fun getProductById(id: String): Mono<Product> {
        return client
            .get()
            .uri("/hello")
            .retrieve()
            .bodyToMono(Product::class.java)
    }


}

@Component
class OrderClient(
    private val client: WebClient = WebClient.builder()
        .baseUrl(ORDER_URL)
        .build()
) {

    fun getOrderById(id: Long): Mono<Order> {
        return client
            .get()
            .uri("/hello/{id}", id)
            .exchange()
            .flatMap { it.bodyToMono(Order::class.java) }
    }
}

@Component
class DeliveryClient(
    private val client: WebClient = WebClient.builder()
        .baseUrl(ORDER_URL)
        .build()
) {

    fun getDeliveryInfo(id: Long): Mono<Delivery> {
        return client
            .get()
            .uri("/hello")
            .retrieve()
            .bodyToMono(Delivery::class.java)
    }
}