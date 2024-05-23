package com.example.quanlykhohang.Model

data class Product(
    val id: Int = 0,
    val name: String? = null,
    val quantity: Int = 0,
    val price: Int = 0,
    val photo: String? = null,
    val storage: String? = null,
    val userID: String? = null
) {
    // Secondary constructor without userID
    constructor(
        id: Int = 0,
        name: String? = null,
        quantity: Int = 0,
        price: Int = 0,
        photo: String? = null,
        storage: String? = null
    ) : this(id, name, quantity, price, photo, storage, null)
}
