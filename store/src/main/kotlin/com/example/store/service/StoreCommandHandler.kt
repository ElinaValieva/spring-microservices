package com.example.store.service

import com.example.common.FailedToReserveProduct
import com.example.common.ProductReserved
import com.example.common.ReserveStoreProductCommand
import com.example.store.exception.StoreException
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
import org.apache.juli.logging.LogFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

class StoreCommandHandler(private val storeService: StoreService) {

    private val logger = LogFactory.getLog(StoreCommandHandler::class.java)

    fun commandHandlerDefinitions(): CommandHandlers = SagaCommandHandlersBuilder
        .fromChannel("storeService")
        .onMessage(ReserveStoreProductCommand::class.java, this::reserve)
        .build()

    private fun reserve(commandMessage: CommandMessage<ReserveStoreProductCommand>): Message? {
        logger.info("Try to reserve product: ${commandMessage.command.productId}")
        return try {
            storeService.receive(commandMessage.command.productId)
            logger.info("Product reserved")
            withSuccess(ProductReserved())
        } catch (e: StoreException) {
            logger.warn("Failed to reserve product")
            withFailure(FailedToReserveProduct())
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
    fun storeService(storeRepository: StoreRepository): StoreService = StoreServiceImpl(storeRepository)

    @Bean
    fun storeCommandHandler(storeService: StoreService): StoreCommandHandler = StoreCommandHandler(storeService)

    @Bean
    fun consumerCommandDispatcher(
        storeCommandHandler: StoreCommandHandler,
        sagaCommandDispatcherFactory: SagaCommandDispatcherFactory
    ): CommandDispatcher =
        sagaCommandDispatcherFactory.make("com.example.order.saga.CreateOrderSaga-consumer", storeCommandHandler.commandHandlerDefinitions())
}