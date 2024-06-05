package com.example.quanlykhohang.Adapter.Delivery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhohang.Model.BillDetail
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ItemDeliveryDetailBinding

class DeliveryBillDetailAdapter(
    private val listBillDetail: ArrayList<BillDetail>,
    private var context: Context
) : RecyclerView.Adapter<DeliveryBillDetailAdapter.DeliveryBillDetailViewHolder>() {
    private lateinit var binding: ItemDeliveryDetailBinding

    inner class DeliveryBillDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeliveryBillDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_delivery_detail, parent, false)
        return DeliveryBillDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listBillDetail.size
    }

    override fun onBindViewHolder(holder: DeliveryBillDetailViewHolder, position: Int) {
        binding = ItemDeliveryDetailBinding.bind(holder.itemView)
        val billDetail = listBillDetail[position]
        binding.txtSanPham.text = "${billDetail.nameProduct}"
        binding.txtSoLuong.text = "${billDetail.quantity}"
        binding.txtGiaXuat.text = "${billDetail.exportPrice}"
    }
}