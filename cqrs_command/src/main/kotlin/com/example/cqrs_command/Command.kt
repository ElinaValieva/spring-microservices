package com.example.cqrs_command

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.eventuate.tram.commands.common.Command

data class ReserveStoreProductCommand @JsonCreator constructor(
    @JsonValue var productId: String
) : Command

data class CreateDeliveryCommand(var orderId: String, var city: String) : Command