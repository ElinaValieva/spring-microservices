package com.example.order.service

import com.example.order.repository.Order
import com.example.order.repository.OrderRepository
import com.example.order.repository.Status
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService(
    private var orderRepository: OrderRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun createOrder(order: Order): Any {
        order.track = UUID.randomUUID().toString()
        kafkaTemplate.send("store", order.product.toString())
        order.status = Status.Accepted
        orderRepository.save(order)
        return order
    }

    fun getOrderInfo(orderId: String) = orderRepository.findById(orderId)
}
