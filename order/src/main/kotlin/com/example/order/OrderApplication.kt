package com.example.order

import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Import

@SpringBootApplication
@EnableDiscoveryClient
@Import(
    SagaOrchestratorConfiguration::class,
    OptimisticLockingDecoratorConfiguration::class,
    EventuateTramKafkaMessageConsumerConfiguration::class,
    TramMessageProducerJdbcConfiguration::class
)
@RefreshScope
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
