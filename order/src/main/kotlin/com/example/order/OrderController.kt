package com.example.order

import com.example.order.repository.Order
import com.example.order.repository.OrderDetails
import com.example.order.repository.Status
import com.example.order.saga.RejectedReason
import com.example.order.service.OrderService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class OrderController(private val orderService: OrderService) {

    @PostMapping("/order")
    fun order(@RequestBody order: OrderDetails) = orderService.createOrder(order)

    @GetMapping("/order/{id}")
    fun getInfo(@PathVariable("id") orderId: Long) = orderService.getOrderInfo(orderId)

    @GetMapping("/hello/{id}")
    fun hello(@PathVariable("id") orderId: Long) = Order(
        id = orderId,
        user = "1",
        product = "2",
        status = Status.Reserved,
        track = UUID.randomUUID().toString(),
        rejectionReason = RejectedReason.DELIVERY_UNAVAILABLE
    )
}