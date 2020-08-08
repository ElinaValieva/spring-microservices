package com.example.store.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class StoreException(override val message: String) : RuntimeException(message)

@ControllerAdvice
class StoreExceptionHandler {

    @ExceptionHandler(StoreException::class)
    fun handleMonitoringException(ex: StoreException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }
}