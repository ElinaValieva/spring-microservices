package com.example.gateway

import com.example.gateway.configuration.Client
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import reactor.core.publisher.Mono


@ExtendWith(MockitoExtension::class)
@WebMvcTest(GatewayController::class)
internal class GatewayControllerTest {

    @MockBean
    private lateinit var client: Client

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun getUserInfo() {
        given(client.getOrderById(1))
            .willReturn(
                Mono.just(
                    Order(
                        id = 1,
                        product = "2",
                        user = "3",
                        status = "Created",
                        track = "12345",
                        rejectionReason = "null"
                    )
                )
            )
        given(client.getDeliveryInfo(1))
            .willReturn(
                Mono.just(
                    Delivery(
                        id = 5,
                        deliveryTrack = "123456",
                        duration = 10,
                        orderId = "1",
                        status = "Updated"
                    )
                )
            )
        given(client.getProductById("2"))
            .willReturn(
                Mono.just(
                    Product(
                        id = 2,
                        description = "description",
                        name = "toy",
                        image = "image",
                        count = 1
                    )
                )
            )
        given(client.getUserById("3"))
            .willReturn(
                Mono.just(
                    User(
                        id = "1234567890",
                        name = "lastName",
                        picture = "picture",
                        locale = "ru",
                        emailVerified = true,
                        email = "email"
                    )
                )
            )

        val response = mockMvc.perform(
            get("/info/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
            .response


        Assertions.assertEquals(200, response.status)
    }
}