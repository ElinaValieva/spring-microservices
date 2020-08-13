package com.example.gateway

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class User @JsonCreator constructor(
    @JsonProperty("id") var id: String,
    @JsonProperty("name") var name: String,
    @JsonProperty("email") var email: String,
    @JsonProperty("picture") var picture: String,
    @JsonProperty("locale") var locale: String,
    @JsonProperty("emailVerified") var emailVerified: Boolean
)

data class Order @JsonCreator constructor(
    @JsonProperty("id") var id: Long,
    @JsonProperty("user") var user: String,
    @JsonProperty("product") var product: String,
    @JsonProperty("status") var status: String,
    @JsonProperty("track") var track: String,
    @JsonProperty("rejectionReason") var rejectionReason: String
)

data class Product @JsonCreator constructor(
    @JsonProperty("id") var id: Long,
    @JsonProperty("description") var description: String,
    @JsonProperty("name") var name: String,
    @JsonProperty("image") var image: String,
    @JsonProperty("count") var count: Int
)

data class Delivery @JsonCreator constructor(
    @JsonProperty("id") var id: Long,
    @JsonProperty("deliveryTrack") var deliveryTrack: String,
    @JsonProperty("duration") var duration: Int,
    @JsonProperty("orderId") var orderId: String,
    @JsonProperty("status") var status: String
)

data class OrderInfo(
    val user: User? = null,
    val order: Order? = null,
    val delivery: Delivery? = null,
    val store: Product? = null
)

data class OrderDetails(
    val userId: String,
    val username: String,
    val email: String,
    val productId: Long,
    val productInfo: String,
    val productLink: String,
    val orderId: Long,
    val orderTrack: String,
    val orderStatus: String,
    val deliveryId: Long,
    val deliveryTrack: String,
    val deliveryStatus: String,
    val deliveryDuration: Int
)

fun convertToOrder(order: Order, user: User, store: Product, delivery: Delivery) =
    OrderDetails(
        userId = user.id,
        username = user.name,
        email = user.email,
        productId = store.id,
        productInfo = "${store.name} ${store.description}",
        productLink = store.image,
        orderId = order.id,
        orderStatus = order.status,
        orderTrack = order.track,
        deliveryId = delivery.id,
        deliveryDuration = delivery.duration,
        deliveryStatus = delivery.status,
        deliveryTrack = delivery.deliveryTrack
    )
