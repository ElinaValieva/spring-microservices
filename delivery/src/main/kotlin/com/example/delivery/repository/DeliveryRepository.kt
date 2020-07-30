package com.example.delivery.repository

import org.springframework.data.annotation.Id
import org.springframework.data.repository.CrudRepository
import javax.persistence.*

interface CityDeliveryRepository : CrudRepository<City, String> {

    fun findByArrival(arrival: String): City?
}

interface DeliveryRepository : CrudRepository<Delivery, String>

@Entity
@Table(name = "delivery", schema = "eventuate")
data class Delivery(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: String? = null,
    @Column(name = "delivery_track") var deliveryTrack: String? = null,
    @OneToOne var city: City? = null,
    var duration: Int,
    @Column(name = "order_id") var orderId: String
)

@Entity
@Table(name = "city", schema = "eventuate")
data class City(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: String? = null,
    var destination: String? = null,
    var arrival: String? = null,
    var duration: Int
)