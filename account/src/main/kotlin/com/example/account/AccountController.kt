package com.example.account

import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RefreshScope
class AccountController {

    @GetMapping("/greeting")
    fun greeting() = "Hello from account"
}