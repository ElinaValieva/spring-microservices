package com.example.order.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class OrderException(override val message: String) : Exception(message)

@ControllerAdvice
class OrderExceptionHandler {

    @ExceptionHandler(OrderException::class)
    fun handleMonitoringException(ex: OrderException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }
}
