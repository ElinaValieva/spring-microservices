package com.example.account.repository

import org.springframework.data.repository.CrudRepository
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "account", schema = "eventuate")
open class Account(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @Column(name = "id") open var id: Long? = null,
    @NotBlank(message = "Username may not be blank") @Column(name = "username") open var username: String = "",
    @NotBlank(message = "Name may not be blank") @Column(name = "first_name") open var firstName: String? = null,
    @NotBlank(message = "Last name may not be blank") @Column(name = "last_name") open var lastName: String? = null,
    @NotBlank(message = "Email may not be blank") @Column(name = "email") open var email: String? = null
)

interface AccountRepository : CrudRepository<Account, Long> {

    fun findByUsername(username: String): Account?
}