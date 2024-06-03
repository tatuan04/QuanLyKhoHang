package com.example.quanlykhohang.Model

data class Bill(
    var id: Int = 0, //mã hóa đơn

    var status: String? = null,// >0, nhập kho, <0 xuất kho

    var createdByUser: String? = null, // id người tạo hóa đơn

    var createdDate: String? = null, // ngày tạo hóa đơn

    var note: String? = null
)