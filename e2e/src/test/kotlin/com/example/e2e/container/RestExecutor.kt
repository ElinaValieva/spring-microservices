package com.example.e2e.container

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity


class RestExecutor {

    private val restTemplate = RestTemplate()

    fun getPresents(url: String): ResponseEntity<List<Product>> = restTemplate.getForEntity("$url/presents", object : ParameterizedTypeReference<List<Product>>() {})

    fun getPresentById(url: String, id: String) = restTemplate.getForEntity("$url/$id", Product::class.java)

    fun createOrder(url: String) = restTemplate.postForEntity("$url/order",
            HttpEntity(OrderDetails(user = "1", city = "Moscow", product = "10")),
            Order::class.java)

    fun getOrderById(url: String, id: String) = restTemplate.getForEntity("$url/order/$id", Order::class.java)

    fun getResource(url: String, resource: String, profile: String) = restTemplate.getForEntity("$url/$resource/$profile", String::class.java)
}

data class OrderDetails(
        var user: String,
        var city: String,
        var product: String
)


data class Product @JsonCreator constructor(
        @JsonProperty("id") var id: Long,
        @JsonProperty("description") var description: String,
        @JsonProperty("name") var name: String,
        @JsonProperty("image") var image: String,
        @JsonProperty("count") var count: Int
)

data class Order @JsonCreator constructor(
        @JsonProperty("id") var id: Long,
        @JsonProperty("user") var user: String,
        @JsonProperty("product") var product: String,
        @JsonProperty("status") var status: String,
        @JsonProperty("track") var track: String,
        @JsonProperty("rejectionReason") var rejectionReason: String
)
