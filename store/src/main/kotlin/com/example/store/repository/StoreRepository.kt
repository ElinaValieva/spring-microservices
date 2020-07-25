package com.example.store.repository

import org.springframework.data.repository.CrudRepository
import javax.persistence.*

@Entity
@Table(name = "product")
open class Product(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) open var id: Long? = null,
    open var description: String? = null,
    open var name: String = "",
    open var image: String? = null,
    open var count: Int = 0
)

interface StoreRepository : CrudRepository<Product, Long>
