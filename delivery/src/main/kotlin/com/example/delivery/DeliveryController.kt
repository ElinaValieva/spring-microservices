package com.example.delivery

import com.example.cqrs_command.BaseSwaggerConfiguration
import com.example.delivery.service.DeliveryService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
open class SwaggerDocumentation(
    basePackage: String = "com.example.delivery",
    service: String = "Delivery"
) : BaseSwaggerConfiguration(basePackage, service)


@RestController
@Api(value = "API for Account service")
class DeliveryController(private val deliveryService: DeliveryService) {

    @GetMapping("/{city}")
    @ApiOperation(value = "Check delivery by city name")
    fun checkDelivery(@PathVariable("city") city: String) = deliveryService.checkDelivery(city)

    @GetMapping("/order/{id}")
    @ApiOperation(value = "Getting delivery information by id")
    fun getDeliveryInfo(@PathVariable("id") id: String) = deliveryService.getDeliveryInfo(id)
}