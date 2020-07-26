package com.example.delivery.service

import com.example.delivery.exception.DeliveryException
import com.example.delivery.repository.CityDeliveryRepository
import com.example.delivery.repository.Delivery
import com.example.delivery.repository.DeliveryRepository
import org.springframework.stereotype.Service

@Service
class DeliveryService(
    private val deliveryRepository: DeliveryRepository,
    private val cityDeliveryRepository: CityDeliveryRepository
) {

    fun checkDelivery(city: String) = cityDeliveryRepository.findByArrival(city)

    fun getDeliveryInfo(id: String) = deliveryRepository.findById(id)

    fun createDelivery(delivery: Delivery): String? {
        val deliveryCity = delivery.city?.arrival?.let { cityDeliveryRepository.findByArrival(it) }
            ?: throw DeliveryException("City not supported")
        return deliveryRepository.save(
            Delivery(
                orderTrack = delivery.orderTrack,
                city = deliveryCity,
                duration = deliveryCity.duration
            )
        ).id
    }
}
