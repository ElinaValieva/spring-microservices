package com.example.delivery.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class DeliveryException(override val message: String) : RuntimeException(message)

@ControllerAdvice
class DeliveryExceptionHandler {

    @ExceptionHandler(DeliveryException::class)
    fun handleMonitoringException(ex: DeliveryException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }
}
