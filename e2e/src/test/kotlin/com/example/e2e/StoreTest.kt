package com.example.e2e

import com.example.e2e.container.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Suppress("CAST_NEVER_SUCCEEDS")
@Testcontainers
class StoreTest {

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

    private val restExecutor = RestExecutor()

    @Test
    fun test() {
        Assertions.assertTrue(storeContainer.isRunning)
        val url = "http://${storeContainer.host}:${storeContainer.firstMappedPort}"
        val presents = restExecutor.getPresents(url)
        val presentsBody = presents.body!!
        Assertions.assertEquals(HttpStatus.OK, presents.statusCode)
        Assertions.assertFalse(presentsBody.isEmpty())
        val present = restExecutor.getPresentById(url, (presentsBody[0] as LinkedHashMap<String, Any>)["id"].toString())
        Assertions.assertEquals(HttpStatus.OK, present.statusCode)
        Assertions.assertNotNull(present.body)
    }
}