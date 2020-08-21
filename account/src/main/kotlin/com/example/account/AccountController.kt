package com.example.account

import com.example.account.repository.Account
import com.example.account.service.AccountService
import com.example.common.BaseSwaggerConfiguration
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerDocumentation(
    basePackage: String = "com.example.account",
    service: String = "Account"
) : BaseSwaggerConfiguration(basePackage, service)


@RestController
@Api(value = "API for Account service")
class AccountController(var accountService: AccountService) {

    @PostMapping("/register")
    @ApiOperation(value = "Registration user")
    fun register(@RequestBody account: Account): ResponseEntity<Account>? {
        return accountService.register(account)?.let { ResponseEntity.ok(it) }
    }

    @GetMapping("/user/{id}")
    @ApiOperation(value = "Getting user information")
    fun getUserInfo(@PathVariable("id") id: String) = accountService.getUserInfo(id)
}