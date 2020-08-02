package com.example.order.service

import com.example.order.repository.Order
import com.example.order.repository.OrderDetails
import com.example.order.repository.OrderRepository
import com.example.order.saga.CreateOrderSaga
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
internal class OrderServiceTest {

    @TestConfiguration
    class OrderServiceTestConfiguration {

        @Bean
        fun orderService(
            orderRepository: OrderRepository,
            orderSaga: CreateOrderSaga,
            sagaInstanceFactory: SagaInstanceFactory
        ) = OrderService(orderRepository, orderSaga, sagaInstanceFactory)
    }

    @Autowired
    private lateinit var orderService: OrderService

    @MockBean
    private lateinit var orderRepository: OrderRepository

    @SpyBean
    private lateinit var orderSaga: CreateOrderSaga

    @MockBean
    private lateinit var sagaInstanceFactory: SagaInstanceFactory

    @Test
    fun createOrder() {
        val orderDetails = OrderDetails(user = "1", city = "Moscow", product = "1")
        val order = Order(id = 1, user = orderDetails.user, product = orderDetails.product)
        Mockito.`when`(orderRepository.save(Order(user = orderDetails.user, product = orderDetails.product)))
            .thenReturn(order)
        Mockito.`when`(orderRepository.findById(1)).thenReturn(Optional.of(order))
        Assertions.assertNull(orderService.createOrder(orderDetails))
    }

    @Test
    fun getOrderInfo() {
        val order = Optional.of(Order())
        Mockito.`when`(orderRepository.findById(1)).thenReturn(order)
        Assertions.assertEquals(order, orderService.getOrderInfo(1))
    }
}