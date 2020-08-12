package com.example.account.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class AccountException(override val message: String) : RuntimeException(message)

@ControllerAdvice
class AccountExceptionHandler {

    @ExceptionHandler(AccountException::class)
    fun handleMonitoringException(ex: AccountException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }
}