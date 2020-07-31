package com.example.store.repository

import org.springframework.data.repository.CrudRepository
import javax.persistence.*

@Entity
@Table(name = "product", schema = "eventuate")
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null,
    var description: String? = null,
    var name: String = "",
    var image: String? = null,
    var count: Int = 0
)

interface StoreRepository : CrudRepository<Product, Long>
