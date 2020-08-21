package com.example.common

import org.springframework.context.annotation.Bean
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

open class BaseSwaggerConfiguration(private var basePackage: String, private var service: String) {

    @Bean
    open fun api(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage(basePackage))
            .build()
            .apiInfo(metaData())
    }

    open fun metaData(): ApiInfo? {
        return ApiInfoBuilder()
            .title("$service API Description")
            .description("API Description for $service")
            .version("1.0")
            .contact(
                Contact(
                    "Valieva Elina",
                    "https://github.com/ElinaValieva/spring-microservices",
                    "veaufa@mail.ru"
                )
            )
            .build()
    }
}
