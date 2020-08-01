package com.example.delivery

import com.example.delivery.service.DeliveryService
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RefreshScope
@RestController
class DeliveryController(private val deliveryService: DeliveryService) {

    @GetMapping("/{city}")
    fun checkDelivery(@PathVariable("city") city: String) = deliveryService.checkDelivery(city)

    @GetMapping("/order/{id}")
    fun getDeliveryInfo(@PathVariable("id") id: String) = deliveryService.getDeliveryInfo(id)
}