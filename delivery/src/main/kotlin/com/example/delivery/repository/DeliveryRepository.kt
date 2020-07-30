package com.example.delivery.repository

import org.springframework.data.repository.CrudRepository
import javax.persistence.*

interface CityDeliveryRepository : CrudRepository<City, Long> {

    fun findByArrival(arrival: String): List<City>
}

interface DeliveryRepository : CrudRepository<Delivery, Long> {

    fun findByOrderId(orderId: String): Delivery?
}

@Entity
@Table(name = "delivery", schema = "eventuate")
data class Delivery(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null,
    @Column(name = "delivery_track") var deliveryTrack: String? = null,
    @OneToOne var city: City? = null,
    var duration: Int = 0,
    @Column(name = "order_id") var orderId: String? = null,
    var status: Status = Status.Created
)

@Entity
@Table(name = "city", schema = "eventuate")
data class City(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null,
    var destination: String? = null,
    var arrival: String? = null,
    var duration: Int = 0
)

enum class Status {
    Created, Updated, Delivered
}