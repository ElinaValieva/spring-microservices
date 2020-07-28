package com.example.order.repository


import com.example.order.saga.RejectedReason
import org.springframework.data.repository.CrudRepository
import javax.persistence.*

interface OrderRepository : CrudRepository<Order, Long>

@Entity
@Table(name = "order")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null,
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