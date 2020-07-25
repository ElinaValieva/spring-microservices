package com.example.store.service

import com.example.store.repository.Product
import com.example.store.repository.StoreRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class StoreService(private val storeRepository: StoreRepository) {

    fun getPresents(): MutableIterable<Product> = storeRepository.findAll()

    @KafkaListener(topics = ["store"])
    fun receive(productId: String) {
        println("Store: $productId")

        if (productId == "")
            return

        val foundedProduct = storeRepository.findById(productId.toLong())
        foundedProduct.ifPresent {
            val product = foundedProduct.get()
            if (product.count == 1)
                storeRepository.delete(product)
            else {
                product.count -= 1
                storeRepository.save(product)
            }
        }
    }
}
