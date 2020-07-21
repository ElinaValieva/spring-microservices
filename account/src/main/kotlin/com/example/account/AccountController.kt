package com.example.account

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController {

    @GetMapping("/")
    fun greeting(): String {
        return "Hello from account"
    }
}