package com.example.order.service

import com.example.order.repository.Order
import com.example.order.repository.OrderDetails
import com.example.order.repository.OrderRepository
import com.example.order.saga.CreateOrderSaga
import com.example.order.saga.CreateOrderSagaData
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private var orderRepository: OrderRepository,
    private val createOrderSaga: CreateOrderSaga,
    private val sagaInstanceFactory: SagaInstanceFactory
) {

    @Transactional
    fun createOrder(order: OrderDetails): Order? {
        val createOrderSagaData = CreateOrderSagaData(orderDetails = order)
        sagaInstanceFactory.create(createOrderSaga, createOrderSagaData)
        return createOrderSagaData.id?.let { orderRepository.findById(it).get() }
    }

    fun getOrderInfo(orderId: Long) = orderRepository.findById(orderId)
}
