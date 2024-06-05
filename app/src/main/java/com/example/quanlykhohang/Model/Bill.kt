package com.example.quanlykhohang.Model

data class Bill(
    val id: Int = 0, //mã hóa đơn

    val status: String? = null,// >0, nhập kho, <0 xuất kho

    val createdByUser: String? = null, // id người tạo hóa đơn

    val createdDate: String="", // ngày tạo hóa đơn

    val note: String? = null
)