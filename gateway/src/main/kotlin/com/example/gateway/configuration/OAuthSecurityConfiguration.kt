package com.example.gateway.configuration

import com.example.gateway.User
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity
@EnableOAuth2Sso
@Configuration
class OAuthSecurityConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .mvcMatchers("/").permitAll()
            .anyRequest().authenticated()
            .and()
            .csrf().disable()
    }

    @Bean
    fun extractPrincipal(client: Client): PrincipalExtractor {
        return PrincipalExtractor {
            val id = it["sub"] as String
            if (!client.getUserById(id).blockOptional().isPresent)
                client.register(mapToUser(it))
        }
    }
}

fun mapToUser(map: MutableMap<String, Any>) = User(
    id = map["sub"] as String,
    name = map["name"] as String,
    email = map["email"] as String,
    locale = map["locale"] as String,
    picture = map["picture"] as String,
    emailVerified = map["email_verified"] as Boolean
)