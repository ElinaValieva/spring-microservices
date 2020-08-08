package com.example.order

import com.example.order.exception.OrderException
import com.example.order.repository.Order
import com.example.order.repository.OrderDetails
import com.example.order.service.OrderService
import com.fasterxml.jackson.databind.ObjectMapper
import io.eventuate.tram.consumer.common.DuplicateMessageDetector
import io.eventuate.tram.messaging.common.ChannelMapping
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockBeans
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.support.TransactionTemplate
import java.util.*

@ExtendWith(MockitoExtension::class)
@WebMvcTest(OrderController::class)
@MockBeans(
    MockBean(ChannelMapping::class),
    MockBean(JdbcTemplate::class),
    MockBean(TransactionTemplate::class),
    MockBean(DuplicateMessageDetector::class)
)
internal class OrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var orderService: OrderService

    @Test
    fun order() {
        val details = OrderDetails(user = "1", city = "Moscow", product = "1")
        val expectedOrder = Order(id = 1)
        given(orderService.createOrder(details)).willReturn(expectedOrder)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/order")
                .content(bodyToJson(details))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(bodyToJson(expectedOrder)))
            .andReturn()
            .response

        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(expectedOrder, bodyToObject(response.contentAsString))
    }

    @Test
    fun orderWithFailing() {
        val details = OrderDetails(user = "1", city = "Moscow", product = "1")
        given(orderService.createOrder(details)).willThrow(OrderException("Order not found"))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/order")
                .content(bodyToJson(details))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Order not found"))
            .andReturn()
            .response

        Assertions.assertEquals(400, response.status)
        Assertions.assertEquals("Order not found", response.contentAsString)
    }

    @Test
    fun getInfo() {
        val expectedOrder = Order(id = 1)
        given(orderService.getOrderInfo(1)).willReturn(Optional.of(expectedOrder))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/order/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(bodyToJson(expectedOrder)))
            .andReturn()
            .response

        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(expectedOrder, bodyToObject(response.contentAsString))
    }

    @Test
    fun getInfoWithFailing() {
        given(orderService.getOrderInfo(1)).willThrow(OrderException("Order not found"))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/order/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Order not found"))
            .andReturn()
            .response

        Assertions.assertEquals(400, response.status)
        Assertions.assertEquals("Order not found", response.contentAsString)
    }

    private fun bodyToJson(account: Any) = ObjectMapper().writer()
        .writeValueAsString(account)

    private fun bodyToObject(json: String) = ObjectMapper().readValue(json, Order::class.java)
}