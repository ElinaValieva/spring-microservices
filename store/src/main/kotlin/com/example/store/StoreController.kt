package com.example.store

import com.example.store.service.StoreService
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RefreshScope
@RestController
class StoreController(private val storeService: StoreService) {

    @GetMapping("/presents")
    fun presents() = storeService.getPresents()

    @GetMapping("/{id}")
    fun test(@PathVariable("id") id: Long) = storeService.getById(id)
}