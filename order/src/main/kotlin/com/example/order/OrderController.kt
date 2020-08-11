package com.example.order

import com.example.cqrs_command.BaseSwaggerConfiguration
import com.example.order.repository.OrderDetails
import com.example.order.service.OrderService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.*
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerDocumentation(
    basePackage: String = "com.example.order",
    service: String = "Order"
) : BaseSwaggerConfiguration(basePackage, service)

@RestController
@RequestMapping("/order")
@Api(value = "API for Account service")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    @ApiOperation(value = "Create order")
    fun order(@RequestBody order: OrderDetails) = orderService.createOrder(order)

    @GetMapping("/{id}")
    @ApiOperation(value = "Get order by id")
    fun getInfo(@PathVariable("id") orderId: Long) = orderService.getOrderInfo(orderId)
}