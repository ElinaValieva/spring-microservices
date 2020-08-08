package com.example.account.service

import com.example.account.exception.AccountException
import com.example.account.repository.Account
import com.example.account.repository.AccountRepository
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface AccountService {

    fun register(account: Account): Account?

    fun login(username: String): Account

    fun edit(account: Account)

    fun getUserInfo(id: Long): Optional<Account>
}

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val accountSaga: AccountSaga,
    private val sagaInstanceFactory: SagaInstanceFactory
) : AccountService {

    @Transactional
    override fun register(account: Account): Account? {
        val foundedUser = accountRepository.findByUsername(account.username)

        if (foundedUser != null)
            throw AccountException("User with same username already exist")

        val sagaData = AccountSagaData(account = account)
        sagaInstanceFactory.create(accountSaga, sagaData)
        return sagaData.id?.let { accountRepository.findById(it).get() }
    }

    override fun login(username: String) =
        accountRepository.findByUsername(username) ?: throw AccountException("User doesn't exist")

    override fun edit(account: Account) {
        val foundedUser = login(account.username)
        foundedUser.firstName = account.firstName
        foundedUser.lastName = account.lastName
        foundedUser.email = account.email

        accountRepository.save(foundedUser)
    }

    override fun getUserInfo(id: Long): Optional<Account> = accountRepository.findById(id)
}
