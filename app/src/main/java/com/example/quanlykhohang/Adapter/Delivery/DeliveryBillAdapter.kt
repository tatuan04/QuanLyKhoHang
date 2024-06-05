package com.example.quanlykhohang.Adapter.Delivery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhohang.Fragment.BottomNavigation.FragDelivery.DeliveryDetailFragment
import com.example.quanlykhohang.Model.Bill
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ItemDeliveryBinding

class DeliveryBillAdapter(private val listBill: ArrayList<Bill>, private var context: Context) :
    RecyclerView.Adapter<DeliveryBillAdapter.DeliveryBillViewHolder>() {
    private lateinit var binding: ItemDeliveryBinding

    inner class DeliveryBillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryBillViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_delivery, parent, false)
        return DeliveryBillViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listBill.size
    }

    override fun onBindViewHolder(holder: DeliveryBillViewHolder, position: Int) {
        binding = ItemDeliveryBinding.bind(holder.itemView)
        val bill = listBill[position]
        binding.txtMa.text = "${bill.id}"
        if (bill.status?.toInt() == 1) {
            binding.txtTrangThai.text = "Xuất kho"
        }
        binding.txtNguoiTao.text = "${bill.createdByUser}"
        binding.txtNgay.text = "${bill.createdDate}"

        binding.btnchitiet.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val fragment = DeliveryDetailFragment()
            val bundle = Bundle()
            bundle.putInt("id", bill.id)
            fragment.arguments = bundle
            activity.supportFragmentManager
                .beginTransaction()
                .add(R.id.fame, fragment)
                .addToBackStack(DeliveryDetailFragment::class.java.name)
                .commit()
        }
    }
}