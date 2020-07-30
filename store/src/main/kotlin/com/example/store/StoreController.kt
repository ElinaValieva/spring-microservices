package com.example.store

import com.example.store.service.StoreService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class StoreController(private val storeService: StoreService) {

    @GetMapping("/presents")
    fun presents() = storeService.getPresents()

    @GetMapping("/{id}")
    fun find(@PathVariable("id") product: String) = storeService.receive(product)
}