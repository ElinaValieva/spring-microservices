package com.example.store.service

import com.example.store.exception.StoreException
import com.example.store.repository.Product
import com.example.store.repository.StoreRepository
import org.apache.commons.logging.LogFactory
import java.util.*

interface StoreService {

    fun getPresents(): MutableIterable<Product>

    fun receive(productId: String)

    fun getById(id: Long): Optional<Product>
}

class StoreServiceImpl(private val storeRepository: StoreRepository) : StoreService {

    private val log = LogFactory.getLog(StoreService::class.java)

    override fun getPresents(): MutableIterable<Product> = storeRepository.findAll()

    override fun receive(productId: String) {
        log.info("Receive product: $productId for reserving")

        val foundedProduct = storeRepository.findById(productId.toLong())

        if (foundedProduct.isEmpty)
            throw StoreException("Product not found")

        removeProduct(foundedProduct.get())
    }

    private fun removeProduct(product: Product) {
        if (product.count == 1)
            storeRepository.delete(product)
        else {
            product.count -= 1
            storeRepository.save(product)
        }
    }

    override fun getById(id: Long): Optional<Product> = storeRepository.findById(id)
}
