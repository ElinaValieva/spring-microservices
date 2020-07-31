package com.example.account.repository

import org.springframework.data.repository.CrudRepository
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "account", schema = "eventuate")
data class Account(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @Column(name = "id") var id: Long? = null,
    @NotBlank(message = "Username may not be blank") @Column(name = "username") var username: String = "",
    @NotBlank(message = "Name may not be blank") @Column(name = "first_name") var firstName: String? = null,
    @NotBlank(message = "Last name may not be blank") @Column(name = "last_name") var lastName: String? = null,
    @NotBlank(message = "Email may not be blank") @Column(name = "email") var email: String? = null,
    @NotBlank(message = "Email may not be blank") @Column(name = "status") var confirmedStatus: Status = Status.EmailNotConfirmed
) {
    fun confirmed() {
        confirmedStatus = Status.EmailConfirmed
    }

    fun rejected() {
        confirmedStatus = Status.WrongEmail
    }
}


enum class Status {
    EmailConfirmed, EmailNotConfirmed, WrongEmail
}

interface AccountRepository : CrudRepository<Account, Long> {

    fun findByUsername(username: String): Account?
}