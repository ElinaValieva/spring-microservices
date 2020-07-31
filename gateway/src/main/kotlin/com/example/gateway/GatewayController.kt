package com.example.gateway

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
class GatewayController(private val client: Client) {

    @GetMapping("/info/{id}")
    fun getUserInfo(
        @PathVariable("id") orderId: Long
    ): Mono<OrderDetails> {
        return Mono
            .zip(
                client.getOrderById(orderId)
                    .map { Optional.of(it) },
                client.getDeliveryInfo(orderId)
                    .map { Optional.of(it) }
            )
            .flatMap {
                Mono.just(
                    OrderInfo(
                        order = it.t1.get(),
                        delivery = it.t2.get()
                    )
                )
            }
            .zipWhen { it.order?.product?.let { it1 -> client.getProductById(it1) } }
            .flatMap {
                Mono.just(
                    OrderInfo(
                        order = it.t1.order,
                        delivery = it.t1.delivery,
                        store = it.t2
                    )
                )
            }
            .zipWhen { it.order?.user?.let { it1 -> client.getUserById(it1) } }
            .flatMap {
                Mono.just(
                    convertToOrder(
                        user = it.t2,
                        store = it.t1.store!!,
                        order = it.t1.order!!,
                        delivery = it.t1.delivery!!
                    )
                )
            }
    }
}