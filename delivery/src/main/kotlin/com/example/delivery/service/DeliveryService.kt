package com.example.delivery.service

import com.example.delivery.exception.DeliveryException
import com.example.delivery.repository.CityDeliveryRepository
import com.example.delivery.repository.Delivery
import com.example.delivery.repository.DeliveryRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeliveryService(
    private val deliveryRepository: DeliveryRepository,
    private val cityDeliveryRepository: CityDeliveryRepository
) {

    fun checkDelivery(city: String) = cityDeliveryRepository.findByArrival(city)

    fun getDeliveryInfo(id: String) = deliveryRepository.findById(id)

    fun createDelivery(city: String, orderId: String) {
        val deliveryCity = cityDeliveryRepository.findByArrival(city)
            ?: throw DeliveryException("City not supported")
        deliveryRepository.save(
            Delivery(
                deliveryTrack = UUID.randomUUID().toString(),
                city = deliveryCity,
                duration = deliveryCity.duration,
                orderId = orderId
            )
        )
    }
}
