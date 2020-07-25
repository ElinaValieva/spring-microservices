package com.example.order.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

interface OrderRepository : MongoRepository<Order, String>

@Document(collection = "order")
data class Order(
    @Id var id: String? = null,
    var user: String? = null,
    var product: String? = null,
    var status: Status = Status.Waiting,
    var track: String? = null
)

enum class Status {
    Waiting, Approved, Accepted
}