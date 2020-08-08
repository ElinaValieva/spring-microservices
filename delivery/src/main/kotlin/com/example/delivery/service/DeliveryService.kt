package com.example.delivery.service

import com.example.delivery.exception.DeliveryException
import com.example.delivery.repository.*
import org.apache.juli.logging.LogFactory
import org.springframework.scheduling.annotation.Scheduled
import java.util.*

interface DeliveryService {
    fun checkDelivery(city: String): City

    fun getDeliveryInfo(id: String): Delivery

    fun createDelivery(city: String, orderId: String)

    fun changeDeliveryDuration()
}

class DeliveryServiceImpl(
    private val deliveryRepository: DeliveryRepository,
    private val cityDeliveryRepository: CityDeliveryRepository
) : DeliveryService {

    private val logger = LogFactory.getLog(DeliveryService::class.java)

    override fun checkDelivery(city: String) = cityDeliveryRepository.findByArrival(city)
        .minBy { it.duration } ?: throw DeliveryException("City not supported")

    override fun getDeliveryInfo(id: String) =
        deliveryRepository.findByOrderId(id) ?: throw DeliveryException("Delivery not found")

    override fun createDelivery(city: String, orderId: String) {
        logger.info("Create delivery: $orderId to $city")
        val deliveryCity = checkDelivery(city)
        deliveryRepository.save(
            Delivery(
                deliveryTrack = UUID.randomUUID().toString(),
                city = deliveryCity,
                duration = deliveryCity.duration,
                orderId = orderId
            )
        )
    }

    @Scheduled(cron = "0 0/60 * * * *")
    override fun changeDeliveryDuration() {
        deliveryRepository.findAll()
            .filter { it.status != Status.Delivered }
            .forEach {
                when (it.duration) {
                    1 -> {
                        it.status = Status.Delivered
                        it.duration = 0
                    }
                    else -> {
                        it.status = Status.Updated
                        it.duration--
                    }
                }
                deliveryRepository.save(it)
            }
    }
}

