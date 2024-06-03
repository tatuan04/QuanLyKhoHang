package com.example.quanlykhohang.Adapter.Receipt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhohang.Model.Bill
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ItemReceiptBinding

class ReceiptBillAdapter(private val listBill: ArrayList<Bill>, private var context: Context) :
    RecyclerView.Adapter<ReceiptBillAdapter.ReceiptBillViewHolder>() {

    private lateinit var binding: ItemReceiptBinding

    inner class ReceiptBillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptBillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receipt, parent, false)
        return ReceiptBillViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listBill.size
    }

    override fun onBindViewHolder(holder: ReceiptBillViewHolder, position: Int) {
        binding = ItemReceiptBinding.bind(holder.itemView)
        val bill = listBill[position]
        binding.txtMa.text = "${bill.id}"
        if (bill.status?.toInt() == 0) {
            binding.txtTrangThai.text = "Nhập kho"
        }
        binding.txtNguoiTao.text = "${bill.createdByUser}"
        binding.txtNgay.text = "${bill.createdDate}"

        binding.btnchitiet.setOnClickListener {

        }

    }
}