package com.example.quanlykhohang.Fragment.DrawerNavigation.FragUser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.quanlykhohang.Model.User
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentUpdateUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst

class UpdateUserFragment : Fragment() {

    private lateinit var binding: FragmentUpdateUserBinding
    private var selectedUri: Uri? = null
    private lateinit var storageReference: StorageReference

    // Phương thức này sẽ được gọi khi view được tạo
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout cho fragment và khởi tạo binding
        val view = inflater.inflate(R.layout.fragment_update_user, container, false)
        binding = FragmentUpdateUserBinding.bind(view)

        // Khởi tạo storageReference cho Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        // Lấy dữ liệu người dùng từ arguments và tạo đối tượng User
        val id = arguments?.getInt("id")
        val email = arguments?.getString("email")
        val password = arguments?.getString("password")
        val userType = arguments?.getString("userType")
        val avatar = arguments?.getString("avatar")
        val user = User(id, email, password, avatar, userType)

        // Hiển thị thông tin người dùng lên UI
        binding.txtEmail.setText(email)
        binding.txtUserType.setText(userType)
        Glide.with(this).load(avatar).into(binding.imgUser)

        // Thiết lập onClickListener cho ảnh người dùng
        binding.imgUser.setOnClickListener {
            openImagePicker()
        }

        // Thiết lập onClickListener cho nút hủy
        binding.btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Thiết lập onClickListener cho nút cập nhật
        binding.btnUpdateUser.setOnClickListener {
            updateUser(user)
        }
        return view
    }

    // Mở trình chọn ảnh
    private fun openImagePicker() {
        FilePickerBuilder.instance
            .setActivityTitle("Chọn ảnh")
            .setMaxCount(1)
            .pickPhoto(this)
    }

    // Cập nhật thông tin người dùng
    private fun updateUser(user: User) {
        val updatedEmail = binding.txtEmail.text.toString()
        val updatedUserType = binding.txtUserType.text.toString()

        if (updatedEmail.isNotEmpty() && updatedUserType.isNotEmpty()) {
            user.email = updatedEmail
            user.userType = updatedUserType
            selectedUri?.let { newImageUri ->
                deleteOldImageAndUploadNew(user, newImageUri)
            }
        } else {
            Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Xóa ảnh cũ và tải lên ảnh mới
    private fun deleteOldImageAndUploadNew(user: User, newImageUri: Uri) {
        val avatarUrl = user.avatar
        val defaultAvatarUrl =
            "https://firebasestorage.googleapis.com/v0/b/musicapplication-451a2.appspot.com/o/profile_images%2Favata_default.jpg?alt=media&token=7d79fd8a-61ad-486f-82c5-e006454ded34"

        if (avatarUrl == defaultAvatarUrl) {
            uploadImageAndSaveUser(user, newImageUri)
        } else {
            if (avatarUrl.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "URL ảnh cũ không hợp lệ", Toast.LENGTH_SHORT)
                    .show()
                Log.e("UpdateUserFragment", "URL ảnh cũ không hợp lệ")
                return
            }
            try {
                val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(avatarUrl)
                oldImageRef.delete().addOnSuccessListener {
                    uploadImageAndSaveUser(user, newImageUri)
                }.addOnFailureListener { error ->
                    Toast.makeText(
                        requireContext(),
                        "Xóa ảnh cũ thất bại: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("UpdateUserFragment", "Xóa ảnh cũ thất bại", error)
                }
            } catch (e: IllegalArgumentException) {
                Toast.makeText(requireContext(), "URL ảnh cũ không hợp lệ", Toast.LENGTH_SHORT)
                    .show()
                Log.e("UpdateUserFragment", "URL ảnh cũ không hợp lệ", e)
            }
        }
    }

    // Tải lên ảnh mới và lưu thông tin người dùng vào cơ sở dữ liệu
    private fun uploadImageAndSaveUser(user: User, newImageUri: Uri) {
        try {
            val filePath =
                storageReference.child("product_images").child("${System.currentTimeMillis()}.jpg")
            filePath.putFile(newImageUri).addOnSuccessListener {
                filePath.downloadUrl.addOnSuccessListener { uri ->
                    user.avatar = uri.toString()
                    updateUserInDatabase(user)
                }
            }.addOnFailureListener { error ->
                Toast.makeText(
                    requireContext(),
                    "Tải ảnh lên thất bại: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("UpdateUserFragment", "Tải ảnh lên thất bại", error)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show()
            Log.e("UpdateUserFragment", "Tải ảnh lên thất bại", e)
        }
    }

    // Cập nhật thông tin người dùng trong cơ sở dữ liệu Firebase
    private fun updateUserInDatabase(user: User) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        myRef.child(user.id.toString()).setValue(user).addOnSuccessListener {
            // Cập nhật email trong Firebase Authentication
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser?.updateEmail(user.email.toString())?.addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Cập nhật thông tin người dùng thành công",
                    Toast.LENGTH_SHORT
                ).show()
                parentFragmentManager.popBackStack() // Quay lại Fragment trước
            }?.addOnFailureListener { error ->
                Toast.makeText(
                    requireContext(),
                    "Cập nhật email thất bại: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("UpdateUserFragment", "Cập nhật email thất bại", error)
            }
        }.addOnFailureListener { error ->
            Toast.makeText(
                requireContext(),
                "Cập nhật thông tin người dùng thất bại",
                Toast.LENGTH_SHORT
            ).show()
            Log.e("UpdateUserFragment", "Cập nhật thông tin người dùng thất bại", error)
        }
    }

    // Xử lý kết quả trả về từ trình chọn ảnh
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            val photos = data?.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
            if (!photos.isNullOrEmpty()) {
                selectedUri = photos[0]
                binding.imgUser.setImageURI(selectedUri)
            }
        }
    }
}
