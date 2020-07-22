package com.example.order

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController {

    @GetMapping("/")
    fun greeting() = "Hello from order"
}