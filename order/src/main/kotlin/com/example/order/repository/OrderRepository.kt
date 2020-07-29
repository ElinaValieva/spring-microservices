package com.example.order.repository


import com.example.order.saga.RejectedReason
import org.springframework.data.repository.CrudRepository
import javax.persistence.*

interface OrderRepository : CrudRepository<Order, Long>

data class OrderDetails(var user: String, var city: String, var product: String)

@Entity
@Table(schema = "eventuate", name = "order")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long = 0,
    @Column(name = "user_id") var user: String? = null,
    var product: String? = null,
    var status: Status = Status.Created,
    var track: String? = null,
    @Column(name = "rejection_reason") var rejectionReason: RejectedReason? = null
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