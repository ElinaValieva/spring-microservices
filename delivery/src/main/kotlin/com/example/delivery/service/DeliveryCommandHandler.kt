package com.example.delivery.service

import com.example.common.CreateDeliveryCommand
import com.example.common.DeliveryCreated
import com.example.common.DeliveryUnavailable
import com.example.delivery.exception.DeliveryException
import com.example.delivery.repository.CityDeliveryRepository
import com.example.delivery.repository.DeliveryRepository
import io.eventuate.tram.commands.consumer.CommandDispatcher
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess
import io.eventuate.tram.commands.consumer.CommandHandlers
import io.eventuate.tram.commands.consumer.CommandMessage
import io.eventuate.tram.messaging.common.Message
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration
import org.apache.juli.logging.LogFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

class DeliveryCommandHandler(private val deliveryService: DeliveryService) {

    private val logger = LogFactory.getLog(DeliveryCommandHandler::class.java)

    fun commandHandlerDefinitions(): CommandHandlers = SagaCommandHandlersBuilder
        .fromChannel("deliveryService")
        .onMessage(CreateDeliveryCommand::class.java, this::deliver)
        .build()

    private fun deliver(commandMessage: CommandMessage<CreateDeliveryCommand>): Message? {
        logger.info("Try to create delivery from ${commandMessage.command.city} for order = ${commandMessage.command.orderId}")
        return try {
            deliveryService.createDelivery(
                orderId = commandMessage.command.orderId,
                city = commandMessage.command.city
            )
            logger.info("Successfully created delivery")
            withSuccess(DeliveryCreated())
        } catch (e: DeliveryException) {
            logger.warn("Failed to created delivery: $e")
            withFailure(DeliveryUnavailable())
        }
    }
}


@Configuration
@Import(
    SagaParticipantConfiguration::class,
    OptimisticLockingDecoratorConfiguration::class,
    EventuateTramKafkaMessageConsumerConfiguration::class,
    TramMessageProducerJdbcConfiguration::class
)
class DeliveryConfiguration {

    @Bean
    fun deliveryService(deliveryRepository: DeliveryRepository, cityDeliveryRepository: CityDeliveryRepository) =
        DeliveryServiceImpl(
            cityDeliveryRepository = cityDeliveryRepository,
            deliveryRepository = deliveryRepository
        )

    @Bean
    fun storeCommandHandler(deliveryService: DeliveryService) = DeliveryCommandHandler(deliveryService)

    @Bean
    fun consumerCommandDispatcher(
        deliveryCommandHandler: DeliveryCommandHandler,
        sagaCommandDispatcherFactory: SagaCommandDispatcherFactory
    ): CommandDispatcher =
        sagaCommandDispatcherFactory.make(
            "deliveryCommandDispatcher",
            deliveryCommandHandler.commandHandlerDefinitions()
        )
}