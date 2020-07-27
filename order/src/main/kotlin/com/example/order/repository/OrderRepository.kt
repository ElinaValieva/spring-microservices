package com.example.order.repository

import com.example.order.saga.RejectedReason
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

interface OrderRepository : MongoRepository<Order, String>

@Document(collection = "order")
data class Order(
    @Id var id: String? = null,
    var user: String? = null,
    var product: String? = null,
    var status: Status = Status.Created,
    var track: String? = null,
    var rejectionReason: RejectedReason? = null
) {

    fun reject(rejectionReason: RejectedReason) {
        this.rejectionReason = rejectionReason
        status = Status.Rejected
    }

    fun approve() {
        status = Status.Approved
    }
}

enum class Status {
    Created, Approved, Rejected
}