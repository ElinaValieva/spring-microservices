package com.example.store.service

import com.example.store.repository.Product
import com.example.store.repository.StoreRepository
import org.bouncycastle.util.StoreException
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
internal class StoreServiceTest {

    @TestConfiguration
    class AccountServiceTestConfiguration {

        @Bean
        fun storeService(storeRepository: StoreRepository) = StoreService(storeRepository)
    }

    @Autowired
    private lateinit var storeService: StoreService

    @MockBean
    private lateinit var storeRepository: StoreRepository

    @Test
    fun getPresents() {
        Mockito.`when`(storeRepository.findAll()).thenReturn(Collections.emptyList())
        Assertions.assertTrue(storeService.getPresents().toList().isEmpty())
    }

    @Test
    fun receive() {
        Mockito.`when`(storeRepository.findById(1)).thenThrow(StoreException::class.java)
        Assertions.assertThrows(StoreException::class.java) { storeService.receive("1") }
    }

    @Test
    fun getById() {
        val product = Optional.of(Product(id = 1, description = "description", name = "toy", count = 10))
        Mockito.`when`(storeRepository.findById(1)).thenReturn(product)
        Assertions.assertEquals(product, storeService.getById(1))
    }
}