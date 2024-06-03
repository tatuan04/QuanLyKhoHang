package com.example.quanlykhohang.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlykhohang.Interface.Notification
import com.example.quanlykhohang.Model.User
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpActivity : AppCompatActivity(), Notification {
    // Khai báo các biến và đối tượng
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var progessDialog: ProgressDialog
    private var userList = ArrayList<User>()
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ánh xạ layout cho activity sử dụng view binding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo ProgressDialog
        progessDialog = ProgressDialog(this)

        // Xử lý sự kiện khi nhấn nút "Cancel" để quay lại màn hình đăng nhập
        binding.btnCancelSU.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Gọi hàm để lấy danh sách người dùng từ Firebase Realtime Database
        getUser()

        // Xử lý sự kiện khi nhấn nút "Sign Up" để đăng ký tài khoản
        binding.btnSignUp.setOnClickListener {
            // Lấy thông tin email và password từ EditText
            email = binding.edtEmailSU.text.toString()
            password = binding.edtPasswordSU.text.toString()

            // Kiem tra thong tin nhap vao
            if (email!!.isEmpty() || password!!.isEmpty()) {
                notificationError("Vui lòng điền đầy đủ thông tin!")
                return@setOnClickListener
            }

            // Tạo mới tài khoản người dùng
            createUser()

            // Thực hiện quá trình đăng ký tài khoản
            SignUp()
        }
    }

    // Hàm để lấy danh sách người dùng từ Firebase Realtime Database
    private fun getUser() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")

        // Lắng nghe sự thay đổi dữ liệu trong "Users"
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (userList != null) {
                    userList.clear()
                }
                // Duyệt qua mỗi user và thêm vào danh sách
                for (snapshot: DataSnapshot in snapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    // Hàm để tạo mới một tài khoản người dùng
    private fun createUser() {
        var size = 0
        var id = 0
        if (userList.isEmpty()) {
            id = 1
        } else {
            size = userList.size - 1
            id = userList[size].id!! + 1
        }
        // Tạo đối tượng User mới với thông tin được nhập
        val newUser = User(
            id,
            email,
            password,
            getString(R.string.linkAnh), // Đây là link ảnh mặc định
            "User"
        )
        // Thêm dữ liệu mới vào Firebase Realtime Database
        addData(newUser)
    }

    // Hàm để thêm dữ liệu người dùng vào Firebase Realtime Database
    private fun addData(user: User) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        val userID = user.id.toString()
        myRef.child(userID).setValue(user)
    }

    // Hàm để thực hiện quá trình đăng ký tài khoản
    private fun SignUp() {
        // Khoi tao FirebaseAuth
        val auth = FirebaseAuth.getInstance()
        // Hien thi ProgressDialog
        progessDialog.show()
        // Tạo một tài khoản người dùng
        auth.createUserWithEmailAndPassword(email.toString(), password.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Hien thi ProgressDialog
                    progessDialog.dismiss()
                    // Nếu đăng ký thành công, chuyển đến MainActivity và kết thúc activity hiện tại
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                    notificationSuccess("Đăng ký thành công!")
                } else {
                    // Nếu đăng ký thất bại, hiển thị thông báo lỗi
                    notificationError("Đăng ký thất bại!")
                }
            }
    }

    // Hàm thông báo thành công
    override fun notificationSuccess(messageSuccess: String) {
        Toast.makeText(this, messageSuccess, Toast.LENGTH_SHORT).show()
    }

    // Hàm thông báo lỗi
    override fun notificationError(messageError: String) {
        Toast.makeText(this, messageError, Toast.LENGTH_SHORT).show()
    }
}
