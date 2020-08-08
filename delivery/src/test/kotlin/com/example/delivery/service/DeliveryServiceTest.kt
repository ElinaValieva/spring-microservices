package com.example.delivery.service

import com.example.delivery.exception.DeliveryException
import com.example.delivery.repository.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
internal class DeliveryServiceTest {

    @TestConfiguration
    class DeliveryServiceTestConfiguration {

        @Bean
        fun deliveryService(cityDeliveryRepository: CityDeliveryRepository, deliveryRepository: DeliveryRepository) =
            DeliveryServiceImpl(deliveryRepository, cityDeliveryRepository)
    }

    @Autowired
    private lateinit var deliveryService: DeliveryService

    @MockBean
    private lateinit var cityDeliveryRepository: CityDeliveryRepository

    @MockBean
    private lateinit var deliveryRepository: DeliveryRepository

    @Test
    fun checkDelivery() {
        val expected = City(id = 1, destination = "A", arrival = "Moscow", duration = 10)
        Mockito.`when`(cityDeliveryRepository.findByArrival("Moscow"))
            .thenReturn(
                listOf(expected, City(id = 2, destination = "B", arrival = "Moscow", duration = 11))
            )
        Assertions.assertEquals(expected, deliveryService.checkDelivery("Moscow"))
    }

    @Test
    fun checkDeliveryForWrongCity() {
        Mockito.`when`(cityDeliveryRepository.findByArrival("Moscow")).thenReturn(Collections.emptyList())
        Assertions.assertThrows(DeliveryException::class.java) { deliveryService.checkDelivery("Moscow") }
    }

    @Test
    fun getDeliveryInfo() {
        val expected = Delivery(id = 1, deliveryTrack = "012345", duration = 10, orderId = "1")
        Mockito.`when`(deliveryRepository.findByOrderId("1")).thenReturn(expected)
        Assertions.assertEquals(expected, deliveryService.getDeliveryInfo("1"))
    }

    @Test
    fun getDeliveryInfoForWrongOrder() {
        Mockito.`when`(deliveryRepository.findByOrderId("1")).thenReturn(null)
        Assertions.assertThrows(DeliveryException::class.java) { deliveryService.getDeliveryInfo("1") }
    }

    @Test
    fun createDelivery() {
        Mockito.`when`(cityDeliveryRepository.findByArrival("Moscow")).thenReturn(Collections.emptyList())
        Assertions.assertThrows(DeliveryException::class.java) { deliveryService.createDelivery("Moscow", "1") }
    }

    @Test
    fun changeDeliveryDuration() {
        Mockito.`when`(deliveryRepository.findAll()).thenReturn(
            listOf(
                Delivery(id = 1, deliveryTrack = "012345", duration = 10, status = Status.Created),
                Delivery(id = 1, deliveryTrack = "012345", duration = 1, status = Status.Updated),
                Delivery(id = 1, deliveryTrack = "012345", duration = 0, status = Status.Delivered)
            )
        )
        deliveryService.changeDeliveryDuration()
        Assertions.assertEquals(
            listOf(
                Delivery(id = 1, deliveryTrack = "012345", duration = 9, status = Status.Updated),
                Delivery(id = 1, deliveryTrack = "012345", duration = 0, status = Status.Delivered),
                Delivery(id = 1, deliveryTrack = "012345", duration = 0, status = Status.Delivered)
            ), deliveryRepository.findAll()
        )
    }
}