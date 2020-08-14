package com.example.e2e

import com.example.e2e.container.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class OrderTest {

    private val network = Network.newNetwork()

    @Container
    val eurekaContainer = EurekaContainer(network)

    @Container
    val configContainer = ConfigContainer(network, eurekaContainer)

    @Container
    val zookeeperContainer = ZookeeperContainer(network)

    @Container
    val kafkaContainer = KafkaContainer(network, zookeeperContainer)

    @Container
    val postgresContainer = PostgresContainer(network)

    @Container
    val cdcContainer = CdcContainer(network,
            zookeeperContainer = zookeeperContainer,
            kafkaContainer = kafkaContainer,
            postgresContainer = postgresContainer)

    @Container
    val storeContainer = StoreContainer(network,
            cdcContainer = cdcContainer,
            eurekaContainer = eurekaContainer,
            configContainer = configContainer)


    @Test
    fun test() {
        Assertions.assertTrue(eurekaContainer.isRunning)
        Assertions.assertTrue(configContainer.isRunning)
        Assertions.assertTrue(zookeeperContainer.isRunning)
        Assertions.assertTrue(cdcContainer.isRunning)
        Assertions.assertTrue(storeContainer.isRunning)
    }
}