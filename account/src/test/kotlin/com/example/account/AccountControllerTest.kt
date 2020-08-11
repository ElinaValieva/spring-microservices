package com.example.account

import com.example.account.exception.AccountException
import com.example.account.repository.Account
import com.example.account.service.AccountService
import com.fasterxml.jackson.databind.ObjectMapper
import io.eventuate.tram.consumer.common.DuplicateMessageDetector
import io.eventuate.tram.messaging.common.ChannelMapping
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockBeans
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.support.TransactionTemplate

@ExtendWith(MockitoExtension::class)
@WebMvcTest(AccountController::class)
@MockBeans(
    MockBean(ChannelMapping::class),
    MockBean(JdbcTemplate::class),
    MockBean(TransactionTemplate::class),
    MockBean(DuplicateMessageDetector::class)
)
internal class AccountControllerTest {

    @MockBean
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun register() {
        val account = Account(username = "user")
        val expectedAccount = Account(id = 1, username = "user")
        given(accountService.register(account)).willReturn(expectedAccount)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/register")
                .content(bodyToJson(account))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(bodyToJson(expectedAccount)))
            .andReturn()
            .response

        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(expectedAccount, bodyToObject(response.contentAsString))
    }

    @Test
    fun registerWithFailing() {
        val account = Account(username = "user")
        given(accountService.register(account)).willThrow(AccountException("User not found"))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/register")
                .content(bodyToJson(account))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("User not found"))
            .andReturn()
            .response

        Assertions.assertEquals(400, response.status)
        Assertions.assertEquals("User not found", response.contentAsString)
    }

    @Test
    fun login() {
        val account = Account(username = "user")
        val expectedAccount = Account(id = 1, username = "user")
        given(accountService.login(account.username)).willReturn(expectedAccount)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(bodyToJson(account))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(bodyToJson(expectedAccount)))
            .andReturn()
            .response

        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(expectedAccount, bodyToObject(response.contentAsString))
    }

    @Test
    fun loginWithFailing() {
        val account = Account(username = "user")
        given(accountService.login(account.username)).willThrow(AccountException("User not found"))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(bodyToJson(account))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("User not found"))
            .andReturn()
            .response

        Assertions.assertEquals(400, response.status)
        Assertions.assertEquals("User not found", response.contentAsString)
    }

    @Test
    fun edit() {
        val account = Account(username = "user")
        Mockito.doNothing().`when`(accountService).edit(account)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/edit")
                .content(bodyToJson(account))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
            .response

        Assertions.assertEquals(200, response.status)
    }

    @Test
    fun editWithFailing() {
        val account = Account(username = "user")
        given(accountService.edit(account)).willThrow(AccountException("User not found"))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/edit")
                .content(bodyToJson(account))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("User not found"))
            .andReturn()
            .response

        Assertions.assertEquals(400, response.status)
        Assertions.assertEquals("User not found", response.contentAsString)
    }

    @Test
    fun getUserInfo() {
        val expectedAccount = Account(id = 1, username = "user")
        given(accountService.getUserInfo(1)).willReturn(expectedAccount)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/user/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(bodyToJson(expectedAccount)))
            .andReturn()
            .response

        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(expectedAccount, bodyToObject(response.contentAsString))
    }

    @Test
    fun getUserInfoWithFailing() {
        given(accountService.getUserInfo(1)).willThrow(AccountException("User not found"))
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/user/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("User not found"))
            .andReturn()
            .response

        Assertions.assertEquals(400, response.status)
        Assertions.assertEquals("User not found", response.contentAsString)
    }

    private fun bodyToJson(account: Account) = ObjectMapper().writer()
        .withDefaultPrettyPrinter()
        .writeValueAsString(account)

    private fun bodyToObject(json: String) = ObjectMapper().readValue(json, Account::class.java)
}