package com.example.account

import com.example.account.repository.Account
import com.example.account.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AccountController(var accountService: AccountService) {

    @PostMapping("/register")
    fun register(@RequestBody account: Account): ResponseEntity<Account>? {
        return accountService.register(account)?.let { ResponseEntity.ok(it) }
    }

    @PostMapping("/login")
    fun login(@RequestBody account: Account) = accountService.login(account.username)

    @PostMapping("/edit")
    fun edit(@RequestBody account: Account): ResponseEntity<HttpStatus> {
        accountService.edit(account)
        return ResponseEntity.ok(HttpStatus.OK)
    }

    @GetMapping("/user/{id}")
    fun getUserInfo(@PathVariable("id") id: Long) = accountService.getUserInfo(id)
}