package com.example.quanlykhohang.Model

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id: Int = 0,
    var name: String? = null, //tên sản phẩm
    var quantity: Int = 0, // số lượng của sản phẩm
    var price: Int = 0, //giá của sản phẩm
    var photo: String? = null, // hình ảnh của sản phẩm khi vào kho
    val storage: String? = null, //lưu trữ
    val userID: String? = null // id của người tạo sản phẩm
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(quantity)
        parcel.writeInt(price)
        parcel.writeString(photo)
        parcel.writeString(storage)
        parcel.writeString(userID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}

