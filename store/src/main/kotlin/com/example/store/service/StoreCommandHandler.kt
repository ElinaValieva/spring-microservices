package com.example.store.service

import com.example.cqrs_command.ProductReservation
import com.example.cqrs_command.ProductReserved
import com.example.cqrs_command.ReserveStoreProductCommand
import io.eventuate.tram.commands.consumer.CommandDispatcher
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess
import io.eventuate.tram.commands.consumer.CommandMessage
import io.eventuate.tram.messaging.common.Message
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration
import org.bouncycastle.util.StoreException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component

@Component
class StoreCommandHandler(private val storeService: StoreService) {

    fun commandHandler() = SagaCommandHandlersBuilder.fromChannel("store")
        .onMessage(ReserveStoreProductCommand::class.java, this::reserve)
        .build()

    private fun reserve(commandMessage: CommandMessage<ReserveStoreProductCommand>): Message? {
        return try {
            storeService.receive(commandMessage.command.productId)
            withSuccess(ProductReserved())
        } catch (e: StoreException) {
            withFailure(ProductReservation())
        }
    }
}


@Configuration
@Import(SagaParticipantConfiguration::class, OptimisticLockingDecoratorConfiguration::class)
class StoreConfiguration {

    @Bean
    fun consumerCommandDispatcher(
        storeCommandHandler: StoreCommandHandler,
        sagaCommandDispatcherFactory: SagaCommandDispatcherFactory
    ): CommandDispatcher =
        sagaCommandDispatcherFactory.make("storeCommandDispatcher", storeCommandHandler.commandHandler())
}