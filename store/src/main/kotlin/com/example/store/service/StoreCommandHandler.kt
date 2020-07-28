package com.example.store.service

import com.example.cqrs_command.ProductReservation
import com.example.cqrs_command.ProductReserved
import com.example.cqrs_command.ReserveStoreProductCommand
import com.example.store.repository.StoreRepository
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
import org.bouncycastle.util.StoreException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

class StoreCommandHandler(private val storeService: StoreService) {

    fun commandHandlerDefinitions(): CommandHandlers = SagaCommandHandlersBuilder
        .fromChannel("storeService")
        .onMessage(ReserveStoreProductCommand::class.java, this::reserve)
        .build()

    private fun reserve(commandMessage: CommandMessage<ReserveStoreProductCommand>): Message? {
        println("Hello from reserve")
        return try {
            storeService.receive(commandMessage.command.productId)
            withSuccess(ProductReserved())
        } catch (e: StoreException) {
            withFailure(ProductReservation())
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
class StoreConfiguration {

    @Bean
    fun storeService(storeRepository: StoreRepository): StoreService = StoreService(storeRepository)

    @Bean
    fun storeCommandHandler(storeService: StoreService): StoreCommandHandler = StoreCommandHandler(storeService)

    @Bean
    fun consumerCommandDispatcher(
        storeCommandHandler: StoreCommandHandler,
        sagaCommandDispatcherFactory: SagaCommandDispatcherFactory
    ): CommandDispatcher =
        sagaCommandDispatcherFactory.make("storeCommandDispatcher", storeCommandHandler.commandHandlerDefinitions())
}