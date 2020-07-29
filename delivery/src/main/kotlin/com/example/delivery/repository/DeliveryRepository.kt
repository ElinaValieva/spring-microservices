package com.example.delivery.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

interface CityDeliveryRepository : MongoRepository<City, String> {

    fun findByArrival(arrival: String): City?
}

interface DeliveryRepository : MongoRepository<Delivery, String>

@Document(collection = "delivery")
data class Delivery(
    @Id var id: String? = null,
    var deliveryTrack: String? = null,
    var city: City? = null,
    var duration: Int,
    var orderId: String
)

@Document(collection = "city")
data class City(
    @Id var id: String? = null,
    var destination: String? = null,
    var arrival: String? = null,
    var duration: Int
)