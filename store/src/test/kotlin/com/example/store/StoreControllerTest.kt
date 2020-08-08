package com.example.store

import com.example.store.exception.StoreException
import com.example.store.repository.Product
import com.example.store.service.StoreService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@ExtendWith(MockitoExtension::class)
@WebMvcTest(StoreController::class)
internal class StoreControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var storeService: StoreService

    @Test
    fun getAllProducts() {
        val list = mutableListOf(Product(id = 1, name = "product"))
        given(storeService.getPresents()).willReturn(list)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/presents")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(bodyToJson(list)))
            .andReturn()
            .response

        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(list, bodyToObjects(response.contentAsString))
    }

    @Test
    fun getProductById() {
        val product = Product(id = 1, name = "product")
        given(storeService.getById(1)).willReturn(Optional.of(product))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(bodyToJson(product)))
            .andReturn()
            .response

        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(product, bodyToObject(response.contentAsString))
    }

    @Test
    fun getProductByIdWithFailing() {
        given(storeService.getById(1)).willThrow(StoreException("Product not found"))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Product not found"))
            .andReturn()
            .response

        Assertions.assertEquals(400, response.status)
        Assertions.assertEquals("Product not found", response.contentAsString)
    }

    private fun bodyToJson(product: Any) = ObjectMapper().writer()
        .writeValueAsString(product)

    private fun bodyToObject(json: String) = ObjectMapper().readValue(json, Product::class.java)

    private fun bodyToObjects(json: String) = ObjectMapper().readValue<List<Product>>(json)

}