package com.example.quanlykhohang.Model

data class User(
    val id: Int? = 0,
    var email: String? = null,
    val password: String? = null,
    var avatar: String? = null,
    var userType: String? = null
)
