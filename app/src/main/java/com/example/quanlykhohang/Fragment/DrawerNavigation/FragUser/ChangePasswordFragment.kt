package com.example.quanlykhohang.Fragment.DrawerNavigation.FragUser

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quanlykhohang.databinding.FragmentChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChangePasswordFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout cho fragment này
        val view = inflater.inflate(com.example.quanlykhohang.R.layout.fragment_change_password, container, false)
        // Khởi tạo binding
        binding = FragmentChangePasswordBinding.bind(view)

        // Xử lý sự kiện nhấn nút "Hủy"
        binding.btnHuyChangePassword.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Xử lý sự kiện nhấn nút "Đổi mật khẩu"
        binding.btnChangePassword.setOnClickListener {
            val oldPassword = binding.edtOldPassword.text.toString().trim()
            val newPassword = binding.edtNewPassword.text.toString().trim()
            val confirmNewPassword = binding.edtConfirmPassword.text.toString().trim()

            // Kiểm tra xem các trường đã được nhập đủ chưa
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            } else if (newPassword != confirmNewPassword) {
                // Kiểm tra xem mật khẩu mới có khớp với mật khẩu xác nhận không
                if (isAdded) {
                    Toast.makeText(requireContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Nếu tất cả điều kiện đều đúng, thực hiện đổi mật khẩu
                changePassword(oldPassword, newPassword)
            }
        }

        return view
    }

    // Hàm thực hiện đổi mật khẩu
    private fun changePassword(oldPassword: String, newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.email != null) {
            // Xác thực lại người dùng bằng email và mật khẩu hiện tại
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Nếu xác thực thành công, cập nhật mật khẩu mới
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Nếu cập nhật mật khẩu thành công, cập nhật trong cơ sở dữ liệu
                                    updatePasswordInDatabase(user.email!!, newPassword)
                                    if (isAdded) {
                                        Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                                        parentFragmentManager.popBackStack()
                                    }
                                } else {
                                    // Nếu cập nhật mật khẩu thất bại, hiển thị thông báo lỗi
                                    task.exception?.message?.let {
                                        if (isAdded) {
                                            Toast.makeText(requireContext(), "Đổi mật khẩu thất bại: $it", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                    } else {
                        // Nếu xác thực thất bại, hiển thị thông báo lỗi
                        task.exception?.message?.let {
                            if (isAdded) {
                                Toast.makeText(requireContext(), "Xác thực thất bại: $it", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        } else {
            // Nếu không tìm thấy người dùng, hiển thị thông báo lỗi
            if (isAdded) {
                Toast.makeText(requireContext(), "Người dùng không tồn tại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hàm cập nhật mật khẩu trong cơ sở dữ liệu
    private fun updatePasswordInDatabase(email: String, newPassword: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("Users")

        // Tìm người dùng có email tương ứng trong cơ sở dữ liệu
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    // Cập nhật mật khẩu mới trong cơ sở dữ liệu
                    userSnapshot.ref.child("password").setValue(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (isAdded) {
                                    Toast.makeText(requireContext(), "Cập nhật mật khẩu trong database thành công", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Nếu cập nhật cơ sở dữ liệu thất bại, hiển thị thông báo lỗi
                                task.exception?.message?.let {
                                    if (isAdded) {
                                        Toast.makeText(requireContext(), "Cập nhật mật khẩu trong database thất bại: $it", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Nếu truy cập cơ sở dữ liệu thất bại, hiển thị thông báo lỗi
                if (isAdded) {
                    Toast.makeText(requireContext(), "Lỗi khi truy cập database: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
