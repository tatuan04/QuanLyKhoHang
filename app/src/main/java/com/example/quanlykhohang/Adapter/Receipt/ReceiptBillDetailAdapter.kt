package com.example.quanlykhohang.Adapter.Receipt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhohang.Model.BillDetail
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ItemReceiptDetailBinding

class ReceiptBillDetailAdapter(
    private val listBillDetail: ArrayList<BillDetail>, private var context: Context
) : RecyclerView.Adapter<ReceiptBillDetailAdapter.ReceiptBillDetailViewHolder>() {
    private lateinit var binding: ItemReceiptDetailBinding

    inner class ReceiptBillDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptBillDetailViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_receipt_detail, parent, false)
        return ReceiptBillDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listBillDetail.size
    }

    override fun onBindViewHolder(holder: ReceiptBillDetailViewHolder, position: Int) {
        binding = ItemReceiptDetailBinding.bind(holder.itemView)
        val billDetail = listBillDetail[position]
        binding.txtSanPham.text = "${billDetail.nameProduct}"
        binding.txtSoLuong.text = "${billDetail.quantity}"
        binding.txtGiaNhap.text = "${billDetail.importPrice}"
    }
}