package com.example.delivery

import com.example.delivery.repository.Delivery
import com.example.delivery.service.DeliveryService
import org.springframework.web.bind.annotation.*

@RestController
class DeliveryController(private val deliveryService: DeliveryService) {

    @GetMapping("/delivery/{city}")
    fun checkDelivery(@PathVariable("city") city: String) = deliveryService.checkDelivery(city)

    @GetMapping("/delivery/{id}")
    fun getDeliveryInfo(@PathVariable("id") id: String) = deliveryService.getDeliveryInfo(id)
}