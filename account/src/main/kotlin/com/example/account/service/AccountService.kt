package com.example.account.service

import com.example.account.exception.AccountException
import com.example.account.repository.Account
import com.example.account.repository.AccountRepository
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val accountSaga: AccountSaga,
    private val sagaInstanceFactory: SagaInstanceFactory
) {

    @Transactional
    fun register(account: Account): Account? {
        val foundedUser = accountRepository.findByUsername(account.username)

        if (foundedUser != null)
            throw AccountException("User with same username already exist")

        val sagaData = AccountSagaData(account = account)
        sagaInstanceFactory.create(accountSaga, sagaData)
        return sagaData.id?.let { accountRepository.findById(it).get() }
    }

    fun login(username: String) =
        accountRepository.findByUsername(username) ?: throw AccountException("User doesn't exist")

    fun edit(account: Account) {
        val foundedUser = login(account.username)
        foundedUser.firstName = account.firstName
        foundedUser.lastName = account.lastName
        foundedUser.email = account.email

        accountRepository.save(foundedUser)
    }

    fun getUserInfo(id: Long): Optional<Account> = accountRepository.findById(id)
}
