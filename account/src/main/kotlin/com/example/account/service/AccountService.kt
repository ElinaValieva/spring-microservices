package com.example.account.service

import com.example.account.exception.AccountException
import com.example.account.repository.Account
import com.example.account.repository.AccountRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service


@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun register(account: Account) {
        val foundedUser = accountRepository.findByUsername(account.username)

        if (foundedUser != null)
            throw AccountException("User with same username already exist")

        accountRepository.save(account)
        notify(account.username)
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

    fun notify(username: String) {
        kafkaTemplate.send("register", username)
    }
}
