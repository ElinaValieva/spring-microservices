package com.example.order.saga

import com.example.cqrs_command.CreateDeliveryCommand
import com.example.cqrs_command.DeliveryUnavailable
import com.example.cqrs_command.ReserveStoreProductCommand
import com.example.order.repository.Order
import com.example.order.repository.OrderRepository
import io.eventuate.tram.commands.consumer.CommandWithDestination
import io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send
import io.eventuate.tram.sagas.orchestration.SagaDefinition
import io.eventuate.tram.sagas.simpledsl.SimpleSaga
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


class CreateOrderSaga(private val orderRepository: OrderRepository) : SimpleSaga<CreateOrderSagaData> {

    override fun getSagaDefinition(): SagaDefinition<CreateOrderSagaData> =
        step()
            .invokeLocal(this::create)
            .withCompensation(this::reject)
            .step()
            .invokeParticipant(this::reserveProduct)
            .onReply(ReserveStoreProductCommand::class.java, this::handleProductReservation)
            .step()
//            .invokeParticipant(this::createDelivery)
//            .onReply(DeliveryUnavailable::class.java, this::handleDeliveryUnavailable)
//            .step()
            .invokeLocal(this::approve)
            .build()

    private fun create(createOrderSagaData: CreateOrderSagaData) {
        val orderData = createOrderSagaData.order
        val order = orderRepository.save(Order(user = orderData.user, product = orderData.product))
        createOrderSagaData.id = order.id!!
    }

    private fun reject(createOrderSagaData: CreateOrderSagaData) {
        createOrderSagaData.rejectionReason?.let {
            createOrderSagaData.id?.let { it1 ->
                orderRepository.findById(it1).get().reject(it)
            }
        }
    }

    private fun reserveProduct(createOrderSagaData: CreateOrderSagaData): CommandWithDestination {
        println("Try to reserve product")
        return send(createOrderSagaData.order.product?.let { ReserveStoreProductCommand(it) })
            .to("my_store")
            .build()
    }

    private fun createDelivery(createOrderSagaData: CreateOrderSagaData): CommandWithDestination =
        send(createOrderSagaData.order.id?.let { CreateDeliveryCommand(order = it.toString()) })
            .to("delivery")
            .build()

    private fun approve(createOrderSagaData: CreateOrderSagaData) {
        createOrderSagaData.rejectionReason?.let {
            createOrderSagaData.id?.let { it1 ->
                orderRepository.findById(it1).get().approve()
            }
        }
    }


    private fun handleProductReservation(
        createOrderSagaData: CreateOrderSagaData,
        productReservation: ReserveStoreProductCommand
    ) {
        println(productReservation)
        createOrderSagaData.rejectionReason = RejectedReason.PRODUCT_ALREADY_RESERVED
    }

    private fun handleDeliveryUnavailable(
        createOrderSagaData: CreateOrderSagaData,
        deliveryUnavailable: DeliveryUnavailable
    ) {
        println(deliveryUnavailable)
        createOrderSagaData.rejectionReason = RejectedReason.DELIVERY_UNAVAILABLE
    }
}

class CreateOrderSagaData(
    var order: Order,
    var id: Long? = null,
    var rejectionReason: RejectedReason? = null
)

enum class RejectedReason {
    PRODUCT_ALREADY_RESERVED, DELIVERY_UNAVAILABLE
}

@Configuration
@Import(
    SagaOrchestratorConfiguration::class,
    OptimisticLockingDecoratorConfiguration::class,
    EventuateTramKafkaMessageConsumerConfiguration::class,
    TramMessageProducerJdbcConfiguration::class
)
class SagaConfiguration {

    @Bean
    fun createOrderSaga(orderRepository: OrderRepository) = CreateOrderSaga(orderRepository)
}