package com.example.quanlykhohang.Model

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id: Int = 0,
    var name: String? = null,
    var quantity: Int = 0,
    var price: Int = 0,
    var photo: String? = null,
    val storage: String? = null,
    val userID: String? = null
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

