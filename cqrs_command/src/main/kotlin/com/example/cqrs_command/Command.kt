package com.example.cqrs_command

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import io.eventuate.tram.commands.common.Command

data class ReserveStoreProductCommand @JsonCreator constructor(
    @JsonValue var productId: String
) : Command

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class CreateDeliveryCommand @JsonCreator constructor(
    @param:JsonProperty("orderId") @get:JsonProperty("orderId") var orderId: String,
    @param:JsonProperty("city") @get:JsonProperty("city") var city: String
) : Command


data class NotifyUserCommand @JsonCreator constructor(
    @JsonValue var username: String
) : Command