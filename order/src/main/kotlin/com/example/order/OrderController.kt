package com.example.order

import com.example.order.repository.OrderDetails
import com.example.order.service.OrderService
import org.springframework.web.bind.annotation.*

@RestController
class OrderController(private val orderService: OrderService) {

    @PostMapping("/order")
    fun order(@RequestBody order: OrderDetails) = orderService.createOrder(order)

    @GetMapping("/order/{id}")
    fun getInfo(@PathVariable("id") orderId: Long) = orderService.getOrderInfo(orderId)
}