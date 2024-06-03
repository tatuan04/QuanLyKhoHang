package com.example.quanlykhohang.Fragment.DrawerNavigation.FragUser

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.quanlykhohang.Interface.TransferFragment
import com.example.quanlykhohang.Model.User
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding
    private lateinit var listUser: ArrayList<User>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        binding = FragmentUserBinding.bind(view)


        // Lấy thông tin người dùng từ Firebase Realtime Database
        val currentUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("Users")

        // Truy vấn người dùng dựa trên email của người dùng hiện tại
        userRef.orderByChild("email").equalTo(currentUser?.email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val email = userSnapshot.child("email").getValue(String::class.java)
                            val userType = userSnapshot.child("userType").getValue(String::class.java)
                            val avatar = userSnapshot.child("avatar").getValue(String::class.java)

                            val user = userSnapshot.getValue(User::class.java)
                            listUser = ArrayList()

                            if (user != null) {
                                listUser.add(user)
                            }

                            // Hiển thị thông tin người dùng trên header của Navigation Drawer
                            binding.txtEmail.text = email
                            binding.txtUesrType.text = userType
                            if (!avatar.isNullOrEmpty()) {
                                Glide.with(this@UserFragment)
                                    .load(avatar)
                                    .into(binding.imgavata)
                            }
                        }
                    } else {
                        Log.e("UserFragment", "Snapshot does not exist")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý khi có lỗi xảy ra khi truy xuất dữ liệu từ Firebase
                    Toast.makeText(
                        requireContext(),
                        "Lỗi khi tải thông tin người dùng",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("MainActivity", "DatabaseError: ${error.message}")
                }
            })

        binding.btnUpdateUser.setOnClickListener {
            if (listUser.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putInt("id", listUser[0].id!!)
                bundle.putString("email", listUser[0].email)
                bundle.putString("password", listUser[0].password)
                bundle.putString("avatar", listUser[0].avatar)
                bundle.putString("userType", listUser[0].userType)
                val fragment = UpdateUserFragment()
                fragment.arguments = bundle

                (requireActivity() as TransferFragment).transferFragment(
                    fragment,
                    "UpdateUserFragment"
                )
            } else {
                Toast.makeText(requireContext(), "Không có người dùng để cập nhật", Toast.LENGTH_SHORT).show()
            }
        }


        binding.changePassword.setOnClickListener {
            (requireActivity() as TransferFragment).transferFragment(
                ChangePasswordFragment(),
                "ChangePasswordFragment"
            )
        }

        binding.listUser.setOnClickListener {
            (requireActivity() as TransferFragment).transferFragment(
                ListUserFragment(),
                "ListUserFragment"
            )
        }

        binding.addUser.setOnClickListener {
            (requireActivity() as TransferFragment).transferFragment(
                AddUserFragment(),
                "AddUserFragment"
            )
        }

        return view
    }

}