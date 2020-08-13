package com.example.account.repository

import org.springframework.data.repository.CrudRepository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "account", schema = "eventuate")
data class Account(
    @Id @Column(name = "id") var id: String? = null,
    @Column(name = "name") var name: String? = null,
    @Column(name = "email") var email: String? = null,
    @Column(name = "picture") var picture: String? = null,
    @Column(name = "locale") var locale: String? = null,
    @Column(name = "emailVerified") var emailVerified: String? = null
) {
    fun confirmed() {
        emailVerified = true.toString()
    }

    fun rejected() {
        emailVerified = false.toString()
    }
}

interface AccountRepository : CrudRepository<Account, String> {

    fun findByName(username: String): Account?
}