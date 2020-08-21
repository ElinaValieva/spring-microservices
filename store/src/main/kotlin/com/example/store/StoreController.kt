package com.example.store

import com.example.common.BaseSwaggerConfiguration
import com.example.store.service.StoreService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerDocumentation(
    basePackage: String = "com.example.store",
    service: String = "Store"
) : BaseSwaggerConfiguration(basePackage, service)

@RestController
@Api(value = "API for Account service")
class StoreController(private val storeService: StoreService) {

    @GetMapping("/presents")
    @ApiOperation(value = "All available products")
    fun getAllProducts() = storeService.getPresents()

    @GetMapping("/{id}")
    @ApiOperation(value = "Getting product by id")
    fun getProductById(@PathVariable("id") id: Long) = storeService.getById(id)
}