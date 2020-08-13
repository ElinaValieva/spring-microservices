package com.example.account.service

import com.example.account.exception.AccountException
import com.example.account.repository.Account
import com.example.account.repository.AccountRepository
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface AccountService {

    fun register(account: Account): Account?

    fun getUserInfo(id: String): Account?
}

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val accountSaga: AccountSaga,
    private val sagaInstanceFactory: SagaInstanceFactory
) : AccountService {

    @Transactional
    override fun register(account: Account): Account? {
        val foundedUser = account.name?.let { accountRepository.findByName(it) }

        if (foundedUser != null)
            throw AccountException("User with same username already exist")

        val sagaData = AccountSagaData(account = account)
        sagaInstanceFactory.create(accountSaga, sagaData)
        return sagaData.id?.let { accountRepository.findById(it).get() }
    }

    override fun getUserInfo(id: String): Account? = accountRepository.findById(id).get()
}
