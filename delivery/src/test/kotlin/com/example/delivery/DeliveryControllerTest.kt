package com.example.delivery

import com.example.delivery.exception.DeliveryException
import com.example.delivery.repository.City
import com.example.delivery.repository.Delivery
import com.example.delivery.service.DeliveryService
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

@ExtendWith(MockitoExtension::class)
@WebMvcTest(DeliveryController::class)
@MockBeans(
    MockBean(ChannelMapping::class),
    MockBean(JdbcTemplate::class),
    MockBean(TransactionTemplate::class),
    MockBean(DuplicateMessageDetector::class)
)
internal class DeliveryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var deliveryService: DeliveryService

    @Test
    fun checkDelivery() {
        val city = City(id = 1)
        given(deliveryService.checkDelivery("Moscow")).willReturn(city)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/{city}", "Moscow")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(bodyToJson(city)))
            .andReturn()
            .response

        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(city, bodyToObject(response.contentAsString, clazz = City::class.java))
    }

    @Test
    fun getDeliveryInfo() {
        val delivery = Delivery(id = 1)
        given(deliveryService.getDeliveryInfo("1")).willReturn(delivery)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/order/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(bodyToJson(delivery)))
            .andReturn().response

        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(delivery, bodyToObject(response.contentAsString, clazz = Delivery::class.java))
    }

    @Test
    fun checkDeliveryWithFailing() {
        given(deliveryService.checkDelivery("Moscow")).willThrow(DeliveryException("Delivery not found"))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/{city}", "Moscow")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Delivery not found"))
            .andReturn()
            .response

        Assertions.assertEquals(400, response.status)
        Assertions.assertEquals("Delivery not found", response.contentAsString)
    }

    @Test
    fun getDeliveryInfoWithFailing() {
        given(deliveryService.getDeliveryInfo("1")).willThrow(DeliveryException("Delivery not found"))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/order/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Delivery not found"))
            .andReturn().response

        Assertions.assertEquals(400, response.status)
        Assertions.assertEquals("Delivery not found", response.contentAsString)
    }


    private fun bodyToJson(account: Any) = ObjectMapper().writer()
        .writeValueAsString(account)

    private fun bodyToObject(json: String, clazz: Class<out Any>) = ObjectMapper().readValue(json, clazz)
}