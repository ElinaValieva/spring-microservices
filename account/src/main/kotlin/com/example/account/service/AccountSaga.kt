package com.example.account.service

import com.example.account.repository.Account
import com.example.account.repository.AccountRepository
import com.example.cqrs_command.FailedToNotify
import com.example.cqrs_command.NotifyUserCommand
import com.example.cqrs_command.UserNotified
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import io.eventuate.tram.commands.consumer.CommandWithDestination
import io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder
import io.eventuate.tram.sagas.orchestration.SagaDefinition
import io.eventuate.tram.sagas.simpledsl.SimpleSaga
import org.springframework.context.annotation.Configuration


@Configuration
class AccountSaga(private val accountRepository: AccountRepository) : SimpleSaga<AccountSagaData> {

    override fun getSagaDefinition(): SagaDefinition<AccountSagaData> =
        step()
            .invokeLocal(this::register)
            .withCompensation(this::reject)
            .step()
            .invokeParticipant(this::notify)
            .onReply(FailedToNotify::class.java, this::handleWrongEmailNotification)
            .onReply(UserNotified::class.java, this::confirmed)
            .build()

    private fun register(accountSagaData: AccountSagaData) {
        val account = accountRepository.save(accountSagaData.account)
        accountSagaData.id = account.id
    }

    private fun notify(accountSagaData: AccountSagaData): CommandWithDestination =
        CommandWithDestinationBuilder.send(accountSagaData.account.email?.let { NotifyUserCommand(it) })
            .to("notificationService")
            .build()

    private fun reject(accountSagaData: AccountSagaData) {
        accountSagaData.id?.let { accountRepository.findById(it).get().rejected() }
    }

    private fun confirmed(accountSagaData: AccountSagaData, userNotified: UserNotified) {
        accountSagaData.id?.let {
            accountRepository.findById(it).get().confirmed()
        }
    }

    private fun handleWrongEmailNotification(
        accountSagaData: AccountSagaData,
        failedToNotify: FailedToNotify
    ) {
        reject(accountSagaData)
    }

}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class AccountSagaData(
    @param:JsonProperty("id") @get:JsonProperty("id") var id: Long? = null,
    @param:JsonProperty("account") @get:JsonProperty("account") var account: Account
)