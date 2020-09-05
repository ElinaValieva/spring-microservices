package com.example.order.saga

import com.example.common.*
import com.example.order.repository.Order
import com.example.order.repository.OrderRepository
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.eventuate.tram.commands.consumer.CommandWithDestination
import io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send
import io.eventuate.tram.sagas.orchestration.SagaDefinition
import io.eventuate.tram.sagas.simpledsl.SimpleSaga
import org.apache.juli.logging.LogFactory
import org.springframework.stereotype.Component

@Component
class CreateOrderSaga(private val orderRepository: OrderRepository) : SimpleSaga<CreateOrderSagaData> {

    private val logger = LogFactory.getLog(CreateOrderSaga::class.java)

    override fun getSagaDefinition(): SagaDefinition<CreateOrderSagaData> =
        step()
            .invokeLocal(this::create)
            .withCompensation(this::reject)
            .step()
            .invokeParticipant(this::reserveProduct)
            .onReply(FailedToReserveProduct::class.java, this::handleProductReservation)
            .onReply(ProductReserved::class.java, this::reserved)
            .step()
            .invokeParticipant(this::createDelivery)
            .onReply(DeliveryUnavailable::class.java, this::handleDeliveryUnavailable)
            .onReply(DeliveryCreated::class.java, this::approve)
            .build()

    private fun create(createOrderSagaData: CreateOrderSagaData) {
        val order = orderRepository.save(
            Order(
                user = createOrderSagaData.user,
                product = createOrderSagaData.product
            )
        )
        createOrderSagaData.id = order.id
    }

    private fun reject(createOrderSagaData: CreateOrderSagaData) {
        createOrderSagaData.rejectionReason?.let {
            createOrderSagaData.id?.let { it1 ->
                orderRepository.findById(it1).get().reject(it)
            }
        }
    }

    private fun reserveProduct(createOrderSagaData: CreateOrderSagaData): CommandWithDestination {
        logger.debug("Try to reserve product: $createOrderSagaData")

        return send(createOrderSagaData.product.let { ReserveStoreProductCommand(it) })
            .to("storeService")
            .build()
    }

    private fun createDelivery(createOrderSagaData: CreateOrderSagaData): CommandWithDestination =
        send(
            CreateDeliveryCommand(
                orderId = createOrderSagaData.id.toString(),
                city = createOrderSagaData.city
            )
        )
            .to("deliveryService")
            .build()

    private fun approve(
        createOrderSagaData: CreateOrderSagaData,
        deliveryCreated: DeliveryCreated
    ) {
        logger.debug("Approved: $deliveryCreated")
        createOrderSagaData.rejectionReason?.let {
            createOrderSagaData.id?.let { it1 ->
                orderRepository.findById(it1).get().approve()
            }
        }
    }

    private fun reserved(
        createOrderSagaData: CreateOrderSagaData,
        productReserved: ProductReserved
    ) {
        logger.debug("Rejected: $productReserved")
        createOrderSagaData.rejectionReason?.let {
            createOrderSagaData.id?.let { it1 ->
                orderRepository.findById(it1).get().reserved()
            }
        }
    }


    private fun handleProductReservation(
        createOrderSagaData: CreateOrderSagaData,
        productReservation: FailedToReserveProduct
    ) {
        logger.debug("Handle: $productReservation")
        createOrderSagaData.rejectionReason = RejectedReason.PRODUCT_WAS_NOT_RESERVED
    }

    private fun handleDeliveryUnavailable(
        createOrderSagaData: CreateOrderSagaData,
        deliveryUnavailable: DeliveryUnavailable
    ) {
        logger.debug("Handle: $deliveryUnavailable")
        createOrderSagaData.rejectionReason = RejectedReason.DELIVERY_UNAVAILABLE
    }
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class CreateOrderSagaData @JsonCreator constructor(
    @param:JsonProperty("city") @get:JsonProperty("city") var city: String,
    @param:JsonProperty("user") @get:JsonProperty("user") var user: String,
    @param:JsonProperty("product") @get:JsonProperty("product") var product: String,
    @param:JsonProperty("id") @get:JsonProperty("id") var id: Long? = null,
    @param:JsonProperty("rejectionReason") @get:JsonProperty("rejectionReason") var rejectionReason: RejectedReason? = null
)

enum class RejectedReason {
    PRODUCT_WAS_NOT_RESERVED, DELIVERY_UNAVAILABLE
}