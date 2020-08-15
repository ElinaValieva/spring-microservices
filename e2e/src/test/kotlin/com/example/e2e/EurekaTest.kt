package com.example.e2e

import com.example.e2e.container.ConfigContainer
import com.example.e2e.container.EurekaContainer
import com.example.e2e.container.GatewayContainer
import com.example.e2e.container.RestExecutor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class EurekaTest {

    private val network = Network.newNetwork()

    @Container
    val eurekaContainer = EurekaContainer(network)

    @Container
    val configContainer = ConfigContainer(network, eurekaContainer)

    @Container
    val gatewayContainer = GatewayContainer(network, eurekaContainer, configContainer)

    private val restExecutor = RestExecutor()

    @Test
    fun test() {
        assertTrue(eurekaContainer.isRunning)
        assertTrue(configContainer.isRunning)
        assertTrue(gatewayContainer.isRunning)
        val url = "http://${configContainer.host}:${configContainer.firstMappedPort}"
        mutableListOf("store", "order", "account", "gateway", "delivery", "notification", "server", "gateway", "admin").forEach {
            assertEquals(HttpStatus.OK, restExecutor.getResource(url, it, "default").statusCode)
        }
    }
}