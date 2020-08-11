package com.example.account.service

import com.example.account.exception.AccountException
import com.example.account.repository.Account
import com.example.account.repository.AccountRepository
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockBeans
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@MockBeans(
    MockBean(AccountSaga::class),
    MockBean(SagaInstanceFactory::class)
)
@ExtendWith(SpringExtension::class)
internal class AccountServiceTest {

    @TestConfiguration
    class AccountServiceTestConfiguration {

        @Bean
        fun accountService(
            accountRepository: AccountRepository,
            accountSaga: AccountSaga,
            sagaInstanceFactory: SagaInstanceFactory
        ): AccountService {
            return AccountServiceImpl(accountRepository, accountSaga, sagaInstanceFactory)
        }
    }

    @Autowired
    private lateinit var accountService: AccountService

    @MockBean
    private lateinit var accountRepository: AccountRepository

    private val account = Account(
        id = 1,
        username = "username",
        firstName = "firstName",
        lastName = "lastName",
        email = "email"
    )

    @Test
    fun register() {
        Mockito.`when`(accountRepository.findByUsername(account.username)).thenReturn(account)
        Assertions.assertThrows(AccountException::class.java) { accountService.register(account) }
    }

    @Test
    fun loginWithUserNotExist() {
        Mockito.`when`(accountRepository.findByUsername(account.username)).thenReturn(null)
        Assertions.assertThrows(AccountException::class.java) { accountService.login(account.username) }
    }

    @Test
    fun login() {
        Mockito.`when`(accountRepository.findByUsername(account.username)).thenReturn(account)
        Assertions.assertEquals(account, accountService.login(account.username))
    }

    @Test
    fun edit() {
        Mockito.`when`(accountRepository.findByUsername(account.username)).thenReturn(null)
        Assertions.assertThrows(AccountException::class.java) { accountService.edit(account) }
    }

    @Test
    fun getUserInfo() {
        Mockito.`when`(account.id?.let { accountRepository.findById(it) }).thenReturn(Optional.of(account))
        Assertions.assertEquals(account, accountService.getUserInfo(1))
    }
}