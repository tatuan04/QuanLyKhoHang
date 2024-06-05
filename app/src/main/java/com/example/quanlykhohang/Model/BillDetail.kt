package com.example.quanlykhohang.Model

data class BillDetail(
    var idProduct: Int = 0, // id sản phẩm

    var idBill: Int = 0, //id hóa đơn

    var nameProduct: String? = null, //tên sản phẩm

    var quantity: Int? = 0, //số lượng sản phẩm

    var importPrice: Int? = 0, //giá nhập

    var exportPrice: Int = 0, //giá xuất

    var createdDate: String? = null //ngày tạo hóa đơn

)
