package com.example.e2e

import com.example.e2e.container.ConfigContainer
import com.example.e2e.container.EurekaContainer
import com.example.e2e.container.GatewayContainer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class EurekaBaseTest {

    private val network = Network.newNetwork()

    @Container
    val eurekaContainer = EurekaContainer(network)

    @Container
    val configContainer = ConfigContainer(network, eurekaContainer)

    @Container
    val gatewayContainer = GatewayContainer(network, eurekaContainer, configContainer)

    @Test
    fun test() {
        Assertions.assertTrue(eurekaContainer.isRunning)
        Assertions.assertTrue(configContainer.isRunning)
        Assertions.assertTrue(gatewayContainer.isRunning)
    }
}