package com.example.delivery.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

interface CityDeliveryRepository : MongoRepository<City, String> {

    fun findByArrival(arrival: String): City?
}

interface DeliveryRepository : MongoRepository<Delivery, String> {

}

@Document(collection = "delivery")
class Delivery(
    @Id var id: String? = null,
    var orderTrack: String? = null,
    var city: City? = null,
    var duration: Int
)

@Document(collection = "city")
class City(
    @Id var id: String? = null,
    var destination: String? = null,
    var arrival: String? = null,
    var duration: Int
)