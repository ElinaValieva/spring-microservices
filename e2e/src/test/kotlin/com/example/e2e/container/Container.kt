package com.example.e2e.container

import com.example.e2e.container.ContainerConstant.CONFIG_REFERENCE
import com.example.e2e.container.ContainerConstant.CONFIG_URL
import com.example.e2e.container.ContainerConstant.EUREKA_REFERENCE
import com.example.e2e.container.ContainerConstant.EUREKA_URL
import com.example.e2e.container.ContainerConstant.HEALTH_CHECK
import com.example.e2e.container.ContainerConstant.PORT_REFERENCE
import com.example.e2e.container.ContainerConstant.PROFILES
import com.example.e2e.container.ContainerConstant.PROFILES_REFERENCE
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait


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
                PORT_REFERENCE to "8008",
                "ZUUL_PREFIX" to "/api"))
        withNetworkAliases("gateway-alias")
        waitingFor(Wait
                .forHttp(HEALTH_CHECK)
                .forStatusCode(200))
    }
}

object ContainerConstant {

    const val EUREKA_REFERENCE = "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE"

    const val EUREKA_URL = "http://server-alias:8761/eureka/"

    const val CONFIG_REFERENCE = "SPRING_CLOUD_CONFIG_URI"

    const val CONFIG_URL = "http://config-alias:8088"

    const val PROFILES_REFERENCE = "SPRING_PROFILES_ACTIVE"

    const val PORT_REFERENCE = "SERVER_PORT"

    const val PROFILES = "prod"

    const val HEALTH_CHECK = "/actuator/health"
}