package com.example.e2e.container

import com.example.e2e.container.ContainerConstant.CONFIG_REFERENCE
import com.example.e2e.container.ContainerConstant.CONFIG_URL
import com.example.e2e.container.ContainerConstant.EUREKA_REFERENCE
import com.example.e2e.container.ContainerConstant.EUREKA_URL
import com.example.e2e.container.ContainerConstant.HEALTH_CHECK
import com.example.e2e.container.ContainerConstant.PROFILES
import com.example.e2e.container.ContainerConstant.PROFILES_REFERENCE
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import java.time.Duration


class EurekaContainer(dockerImageName: String) : GenericContainer<EurekaContainer>(dockerImageName) {

    constructor(network: Network) : this("elvaliev/server") {
        addExposedPorts(8761)
        withNetwork(network)
        withNetworkAliases("server-alias")
        waitingFor(Wait
            .forHttp(HEALTH_CHECK)
            .forStatusCode(200))
    }
}

class ConfigContainer(dockerImageName: String) : GenericContainer<EurekaContainer>(dockerImageName) {

    constructor(network: Network, eurekaContainer: EurekaContainer) : this("elvaliev/config") {
        addExposedPorts(8088)
        withNetwork(network)
        dependsOn(eurekaContainer)
        withEnv(EUREKA_REFERENCE, EUREKA_URL)
        withNetworkAliases("config-alias")
        waitingFor(Wait
            .forHttp(HEALTH_CHECK)
            .forStatusCode(200))
    }
}

class GatewayContainer(dockerImageName: String) : GenericContainer<EurekaContainer>(dockerImageName) {

    constructor(network: Network,
                eurekaContainer: EurekaContainer,
                configContainer: ConfigContainer) : this("elvaliev/gateway") {
        addExposedPorts(8008)
        withNetwork(network)
        dependsOn(eurekaContainer, configContainer)
        withEnv(mutableMapOf(
            EUREKA_REFERENCE to EUREKA_URL,
            CONFIG_REFERENCE to CONFIG_URL,
            PROFILES_REFERENCE to PROFILES,
            "ZUUL_PREFIX" to "/api"))
        withNetworkAliases("gateway-alias")
        waitingFor(Wait
            .forHttp(HEALTH_CHECK)
            .forStatusCode(200))
    }
}

class ZookeeperContainer(dockerImageName: String) : GenericContainer<EurekaContainer>(dockerImageName) {

    constructor(network: Network) : this("confluentinc/cp-zookeeper:5.2.4") {
        addExposedPorts(2181)
        withNetwork(network)
        withEnv(mutableMapOf(
            "ZOOKEEPER_CLIENT_PORT" to "2181",
            "KAFKA_HEAP_OPTS" to "-Xmx64m"))
        withNetworkAliases("zookeeper")
    }
}

class KafkaContainer(dockerImageName: String) : GenericContainer<EurekaContainer>(dockerImageName) {

    constructor(network: Network, zookeeperContainer: ZookeeperContainer) : this("confluentinc/cp-kafka:5.2.4") {
        addExposedPorts(9092)
        withNetwork(network)
        dependsOn(zookeeperContainer)
        withEnv(mutableMapOf(
            "KAFKA_LISTENERS" to "LC://kafka:29092,LX://kafka:9092",
            "KAFKA_ADVERTISED_LISTENERS" to "LC://kafka:29092,LX://kafka:9092",
            "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP" to "LC:PLAINTEXT,LX:PLAINTEXT",
            "KAFKA_INTER_BROKER_LISTENER_NAME" to "LC",
            "KAFKA_ZOOKEEPER_CONNECT" to "zookeeper:2181"))
        withNetworkAliases("kafka")
    }
}

class PostgresContainer(dockerImageName: String) : GenericContainer<EurekaContainer>(dockerImageName) {

    constructor(network: Network) : this("eventuateio/eventuate-tram-sagas-postgres") {
        addExposedPorts(5432)
        withNetwork(network)
        withEnv(mutableMapOf(
            "POSTGRES_USER" to "eventuate",
            "POSTGRES_PASSWORD" to "eventuate"))
        withNetworkAliases("postgres")
    }
}

class CdcContainer(dockerImageName: String) : GenericContainer<EurekaContainer>(dockerImageName) {

    constructor(network: Network,
                zookeeperContainer: ZookeeperContainer,
                kafkaContainer: KafkaContainer,
                postgresContainer: PostgresContainer) : this("eventuateio/eventuate-cdc-service:0.6.0.RC3") {
        addExposedPorts(8080)
        withNetwork(network)
        dependsOn(zookeeperContainer, kafkaContainer, postgresContainer)
        withEnv(mutableMapOf(
            "SPRING_DATASOURCE_URL" to "jdbc:postgresql://postgres/eventuate",
            "SPRING_DATASOURCE_USERNAME" to "eventuate",
            "SPRING_DATASOURCE_PASSWORD" to "eventuate",
            "SPRING_DATASOURCE_TEST_ON_BORROW" to "true",
            "SPRING_DATASOURCE_VALIDATION_QUERY" to "SELECT 1",
            "SPRING_DATASOURCE_DRIVER_CLASS_NAME" to "org.postgresql.Driver",
            "EVENTUATELOCAL_KAFKA_BOOTSTRAP_SERVERS" to "kafka:29092",
            "EVENTUATELOCAL_ZOOKEEPER_CONNECTION_STRING" to "zookeeper:2181",
            "EVENTUATELOCAL_CDC_READER_NAME" to "PostgresPollingReader",
            "SPRING_PROFILES_ACTIVE" to "EventuatePolling",
            "JAVA_OPTS" to "-Xmx64m"))
        withNetworkAliases("cdc")
    }
}

class StoreContainer(dockerImageName: String) : GenericContainer<EurekaContainer>(dockerImageName) {

    constructor(network: Network,
                eurekaContainer: EurekaContainer,
                configContainer: ConfigContainer,
                cdcContainer: CdcContainer) : this("elvaliev/store") {
        addExposedPorts(8082)
        withNetwork(network)
        dependsOn(cdcContainer, eurekaContainer, configContainer)
        withEnv(mutableMapOf(
            EUREKA_REFERENCE to EUREKA_URL,
            CONFIG_REFERENCE to CONFIG_URL,
            PROFILES_REFERENCE to PROFILES))
        withNetworkAliases("store")
        waitingFor(Wait
            .forHttp(HEALTH_CHECK)
            .forStatusCode(200))
    }
}

class OrderContainer(dockerImageName: String) : GenericContainer<EurekaContainer>(dockerImageName) {

    constructor(network: Network,
                eurekaContainer: EurekaContainer,
                configContainer: ConfigContainer,
                cdcContainer: CdcContainer) : this("elvaliev/order") {
        addExposedPorts(8083)
        withNetwork(network)
        dependsOn(cdcContainer, eurekaContainer, configContainer)
        withEnv(mutableMapOf(
            EUREKA_REFERENCE to EUREKA_URL,
            CONFIG_REFERENCE to CONFIG_URL,
            PROFILES_REFERENCE to PROFILES))
        withNetworkAliases("order")
        waitingFor(Wait
            .forHttp(HEALTH_CHECK)
            .forStatusCode(200))
    }
}

class DeliveryContainer(dockerImageName: String) : GenericContainer<EurekaContainer>(dockerImageName) {

    constructor(network: Network,
                eurekaContainer: EurekaContainer,
                configContainer: ConfigContainer,
                cdcContainer: CdcContainer) : this("elvaliev/delivery") {
        addExposedPorts(8084)
        withNetwork(network)
        dependsOn(cdcContainer, eurekaContainer, configContainer)
        withEnv(mutableMapOf(
            EUREKA_REFERENCE to EUREKA_URL,
            CONFIG_REFERENCE to CONFIG_URL,
            PROFILES_REFERENCE to PROFILES))
        withNetworkAliases("delivery")
        waitingFor(Wait
            .forHttp(HEALTH_CHECK)
            .withReadTimeout(Duration.ofMinutes(2))
            .forStatusCode(200))
    }
}

object ContainerConstant {

    const val EUREKA_REFERENCE = "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE"

    const val EUREKA_URL = "http://server-alias:8761/eureka/"

    const val CONFIG_REFERENCE = "SPRING_CLOUD_CONFIG_URI"

    const val CONFIG_URL = "http://config-alias:8088"

    const val PROFILES_REFERENCE = "SPRING_PROFILES_ACTIVE"

    const val PROFILES = "prod"

    const val HEALTH_CHECK = "/actuator/health"
}