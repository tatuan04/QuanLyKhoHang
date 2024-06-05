package com.example.quanlykhohang.Adapter.User

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quanlykhohang.Model.User
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ItemUserBinding

class UserAdpater(private val listUser: ArrayList<User>, private var context: Context) :
    RecyclerView.Adapter<UserAdpater.UserViewHolder>() {
    private lateinit var binding: ItemUserBinding

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        binding = ItemUserBinding.bind(holder.itemView)
        val user = listUser[position]
        binding.txtId.text = "Mã người dùng: ${user.id}"
        binding.txtEmail.text = "Email: ${user.email}"
        binding.txtPassword.text = "Mật khẩu: ${user.password}"
        binding.txtUesrType.text = "Loại người dùng: ${user.userType}"
        val photoPath = user.avatar
        if (photoPath != null) {
            Glide.with(binding.imgUser.context)
                .load(Uri.parse(photoPath))
                .into(binding.imgUser)
        } else {
            binding.imgUser.setImageResource(R.drawable.img)
        }
    }
}