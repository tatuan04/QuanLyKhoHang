package com.example.quanlykhohang.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quanlykhohang.Model.Product
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ActivitySignUpBinding
import com.example.quanlykhohang.databinding.ItemProductBinding

class ProductAdapter(private var listProduct: ArrayList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private lateinit var binding: ItemProductBinding

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listProduct.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        binding = ItemProductBinding.bind(holder.itemView)
        val product = listProduct[position]
        binding.txtmaSP.text = "Mã sản phẩm: ${product.id}"
        binding.txtTenSP.text = "Tên sản phẩm: ${product.name}"
        binding.txtGiaSP.text = "Giá sản phẩm: ${product.price}"
        binding.txtSoLuong.text = "Số lượng: ${product.quantity}"
        binding.txtNguoiThem.text = "Người thêm: ${product.userID}"

        val photoPath = product.photo
        if (photoPath != null) {
            Glide.with(binding.imgHinhSP.context)
                .load(Uri.parse(photoPath))
                .into(binding.imgHinhSP)
        } else {
            binding.imgHinhSP.setImageResource(R.drawable.img) // Đặt hình ảnh mặc định
        }

        binding.foldingCell.setOnClickListener {
            binding.foldingCell.toggle(false)
        }

    }
    fun setData(newData: ArrayList<Product>) {
        if (newData.isEmpty()) return
        listProduct = newData
        notifyDataSetChanged()
    }

}