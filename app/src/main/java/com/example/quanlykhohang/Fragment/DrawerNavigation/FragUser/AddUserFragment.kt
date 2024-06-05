package com.example.quanlykhohang.Fragment.DrawerNavigation.FragUser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import com.example.quanlykhohang.Activity.MainActivity
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.Interface.Notification
import com.example.quanlykhohang.Model.Product
import com.example.quanlykhohang.Model.User
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentAddUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class AddUserFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentAddUserBinding
    private var listUri = arrayListOf<Uri>()
    private var userList = ArrayList<User>()
    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_user, container, false)
        binding = FragmentAddUserBinding.bind(view)

        storageReference = FirebaseStorage.getInstance().reference

        setHasOptionsMenu(true)
        // Thông báo rằng fragment này có một menu tùy chọn

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        // Lấy ActionBar từ activity đang chứa

        requireNotNull(actionBar) { "Action bar is null" }
        // Đảm bảo rằng ActionBar không null

        actionBar.setDisplayHomeAsUpEnabled(true)
        // Bật nút home như một nút "up"

        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        // Đặt biểu tượng của nút home thành biểu tượng mũi tên quay lại tùy chỉnh

        binding.imgAvatar.setOnClickListener {
            grantPermissions()
        }

        getUser()

        binding.btnAdd.setOnClickListener {
            getUserID()
        }


        return view
    }

    private fun grantPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )

        // Kiểm tra nếu quyền đã được cấp
        if (EasyPermissions.hasPermissions(requireContext(), *permissions)) {
            imagePicker()  // Mở trình chọn hình ảnh nếu quyền đã được cấp
        } else {
            // Yêu cầu quyền
            EasyPermissions.requestPermissions(
                this,
                "Cấp quyền truy cập ảnh",
                100,
                *permissions
            )
        }
    }

    // Mở trình chọn hình ảnh để chọn hình ảnh
    private fun imagePicker() {
        FilePickerBuilder.instance
            .setActivityTitle("Chọn ảnh")  // Đặt tiêu đề cho trình chọn hình ảnh
            .setSpan(
                FilePickerConst.SPAN_TYPE.FOLDER_SPAN,
                3
            )  // Đặt khoảng cách cho chế độ xem thư mục
            .setSpan(
                FilePickerConst.SPAN_TYPE.DETAIL_SPAN,
                4
            )  // Đặt khoảng cách cho chế độ xem chi tiết
            .setMaxCount(1)  // Đặt số lượng hình ảnh tối đa có thể chọn
            .setSelectedFiles(listUri)  // Truyền URI đã chọn
            .setActivityTheme(R.style.CustomTheme)  // Đặt chủ đề cho trình chọn hình ảnh
            .pickPhoto(this)  // Bắt đầu trình chọn hình ảnh
    }

    private fun getUser() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (userList != null) {
                    userList.clear()
                }
                for (snapshot: DataSnapshot in snapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    // Lấy ID người dùng hiện tại và tải lên hình ảnh
    private fun getUserID() {
        val users: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (users == null) {
            return  // Trả về nếu không có người dùng đăng nhập
        }
        val email = users.email

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in snapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user?.email.equals(email)) {
                        if (user != null) {
                            uploadImage()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    // Tải lên hình ảnh đến Firebase Storage
    private fun uploadImage() {
        if (listUri.isNotEmpty()) {
            val filePath =
                storageReference.child("user_images").child("${System.currentTimeMillis()}.jpg")
            filePath.putFile(listUri[0])
                .addOnSuccessListener { taskSnapshot ->
                    filePath.downloadUrl.addOnSuccessListener { uri ->
                        if (isAdded) {
                            addUser(uri.toString())  // Thêm sản phẩm với URL hình ảnh
                        } else {
                            Log.w("AddUserFragment", "Fragment not attached to context")
                        }
                    }
                }
                .addOnFailureListener { error ->
                    if (isAdded) {
                        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w("AddUserFragment", "Fragment not attached to context")
                    }
                }
        } else {
            if (isAdded) {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Log.w("AddUserFragment", "Fragment not attached to context")
            }
        }
    }


    // Hàm kiểm tra mật khẩu
    private fun isPasswordValid(password: String, confirmPassword: String): Boolean {
        // Kiểm tra nếu mật khẩu và mật khẩu nhập lại không khớp
        if (password != confirmPassword) {
            return false
        }
        // Điều kiện kiểm tra mật khẩu (ví dụ: ít nhất 6 ký tự, chứa ít nhất một chữ cái và một số)
        if (password.length < 6) {
            return false
        }
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    // Thêm sản phẩm vào cơ sở dữ liệu
    private fun addUser(imageUrl: String) {
        var size = 0
        var id = 0
        if (userList.isEmpty()) {
            id = 1
        } else {
            size = userList.size - 1
            id = userList[size].id!! + 1
        }

        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        val confirmPassword = binding.txtPassword2.text.toString()

        // Kiểm tra mật khẩu
        if (!isPasswordValid(password, confirmPassword)) {
            Toast.makeText(requireContext(), "Mật không hợp lệ hoặc không khốp", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val newUser = User(
                        id,
                        email,
                        password,
                        imageUrl,
                        "user"
                    )
                    addData(newUser)  // Thêm dữ liệu người dùng vào cơ sở dữ liệu
                } else {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            task.exception?.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Log.w("AddUserFragment", "Fragment not attached to context")
                    }
                }
            }
    }

    // Thêm dữ liệu người dùng vào Firebase Database
    private fun addData(user: User) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        val userID = user.id.toString()

        myRef.child(userID).setValue(user)
            .addOnSuccessListener {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Add data successfully", Toast.LENGTH_SHORT)
                        .show()
                    parentFragmentManager.popBackStack()
                } else {
                    Log.w("AddUserFragment", "Fragment not attached to context")
                }
            }
            .addOnFailureListener { error ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to add data", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("AddUserFragment", "Failed to add data", error)
                } else {
                    Log.w("AddUserFragment", "Fragment not attached to context")
                }
            }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            listUri = data?.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)!!
            binding.imgAvatar.setImageURI(listUri[0])
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == 100 && perms.size == 1) {
            imagePicker()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Ghi đè phương thức onOptionsItemSelected để xử lý các lựa chọn mục menu

        val id = item.itemId
        // Lấy ID của mục menu được chọn

        if (id == android.R.id.home) {
            // Nếu mục được chọn là nút home (ID android.R.id.home)

            requireActivity().supportFragmentManager.popBackStack()
            // Pop back stack của fragment để trở về fragment trước đó

            closeMenu()
            // Gọi phương thức closeMenu để đóng menu

            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            // Lấy lại ActionBar từ activity đang chứa

            requireNotNull(actionBar) { "Action bar is null" }
            // Đảm bảo rằng ActionBar không null

            actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
            // Đặt lại biểu tượng của nút home thành biểu tượng menu

            return true
            // Trả về true để xác nhận rằng sự kiện đã được xử lý
        }

        return super.onOptionsItemSelected(item)
        // Nếu mục được chọn không phải là nút home, chuyển sự kiện cho thực thi của lớp cha
    }

    private fun closeMenu() {
        // Phương thức trợ giúp để đóng menu

        (requireActivity() as MenuControl).closeMenu()
        // Ép kiểu activity thành MenuControl và gọi phương thức closeMenu của nó
    }
}

