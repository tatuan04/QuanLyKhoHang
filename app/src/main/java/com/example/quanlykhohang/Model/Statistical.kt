package com.example.quanlykhohang.Model

data class Statistical(
    var date: String = "",
    val tongVao: Int = 0,
    val tongRa: Int = 0,
    val tongDoanhThu: Double = 0.0
) {
    constructor(tongDoanhThuu: Double) : this(tongDoanhThu = tongDoanhThuu)
    constructor(date: String, tongDoanhThuu: Double) : this(
        date = date,
        tongDoanhThu = tongDoanhThuu
    )
}