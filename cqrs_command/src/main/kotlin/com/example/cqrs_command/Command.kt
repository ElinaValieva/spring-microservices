package com.example.cqrs_command

import io.eventuate.tram.commands.common.Command

data class ReserveStoreProductCommand(var productId: String) : Command

data class CreateDeliveryCommand(var order: String) : Command
