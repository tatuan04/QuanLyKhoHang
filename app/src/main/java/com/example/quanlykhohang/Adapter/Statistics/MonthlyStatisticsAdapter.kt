package com.example.quanlykhohang.Adapter.Statistics

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhohang.Model.Statistical
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ItemMonthlyStatisticsBinding

class MonthlyStatisticsAdapter(
    private val listStatistical: ArrayList<Statistical>,
    private val context: Context
) : RecyclerView.Adapter<MonthlyStatisticsAdapter.MonthlyStatisticsViewHolder>() {
    private lateinit var binding: ItemMonthlyStatisticsBinding

    inner class MonthlyStatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyStatisticsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_statistics, parent, false)
        return MonthlyStatisticsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listStatistical.size
    }

    override fun onBindViewHolder(holder: MonthlyStatisticsViewHolder, position: Int) {
        binding = ItemMonthlyStatisticsBinding.bind(holder.itemView)
        val statistical = listStatistical[position]
        binding.txtNgay.text = statistical.date
        binding.txtNhapKho.text = statistical.tongVao.toString()
        binding.txtXuatKho.text = statistical.tongRa.toString()
        binding.txtDoanhThu.text = statistical.tongDoanhThu.toString()
        binding.btnchitiet.setOnClickListener {
        }
    }
}