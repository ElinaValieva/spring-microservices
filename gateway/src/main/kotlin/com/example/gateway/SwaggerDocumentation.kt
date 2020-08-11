package com.example.gateway

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import springfox.documentation.swagger.web.SwaggerResource
import springfox.documentation.swagger.web.SwaggerResourcesProvider
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Primary
@Configuration
@EnableSwagger2
class SwaggerDocumentation : SwaggerResourcesProvider {

    @Value("\${zuul.prefix}")
    lateinit var prefixApi: String

    companion object SwaggerDocumentationConstants {
        const val version = "2.0"
        const val pathPattern = "v2/api-docs"
        val services = listOf("account", "order", "store", "delivery")
    }

    override fun get(): MutableList<SwaggerResource> {
        return services.map { swaggerResource(it) }.toMutableList()
    }

    private fun swaggerResource(name: String): SwaggerResource {
        val swaggerResource = SwaggerResource()
        swaggerResource.name = name
        swaggerResource.location = "$prefixApi/$name/$pathPattern"
        swaggerResource.swaggerVersion = version
        return swaggerResource
    }
}