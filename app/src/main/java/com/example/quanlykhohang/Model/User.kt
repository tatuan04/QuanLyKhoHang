package com.example.quanlykhohang.Model

import android.provider.ContactsContract.CommonDataKinds.Email
import android.security.identity.AccessControlProfileId

data class User(
    val id: Int? = 0,
    val email: String? = null,
    val password: String? = null,
    val avatar: String? = null
)
