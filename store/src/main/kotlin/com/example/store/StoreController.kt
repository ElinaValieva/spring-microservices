package com.example.store

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StoreController {

    @GetMapping("/")
    fun greeting() = "Hello from store"
}