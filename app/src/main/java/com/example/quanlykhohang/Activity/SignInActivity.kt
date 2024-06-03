package com.example.quanlykhohang.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat.startActivity
import com.example.quanlykhohang.Interface.Notification
import com.example.quanlykhohang.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity(), Notification {
    // Khai báo biến và đối tượng
    private lateinit var binding: ActivitySignInBinding
    private lateinit var progessDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ánh xạ layout cho activity sử dụng view binding
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo ProgressDialog
        progessDialog = ProgressDialog(this)

        // Xử lý sự kiện khi nhấn nút "Sign Up" để chuyển đến màn hình đăng ký
        binding.btnAsk.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Xử lý sự kiện khi nhấn nút "Cancel" để quay lại màn hình chào mừng
        binding.btnCancelSI.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Xử lý sự kiện khi nhấn nút "Sign In" để đăng nhập
        binding.btnSignIn.setOnClickListener {
            SignIn()
        }
    }

    // Hàm để thực hiện quá trình đăng nhập
    private fun SignIn() {
        // Lấy thông tin email và password từ EditText
        val email = binding.edtEmailSI.text.toString()
        val password = binding.edtPasswordSI.text.toString()

        // Kiem tra thong tin nhap vao
        if (email.isEmpty() || password.isEmpty()) {
            notificationError("Vui lòng điền đầy đủ thông tin!")
            return
        }

        // Tạo đối tượng FirebaseAuth
        val auth = FirebaseAuth.getInstance()

        // Hiển thị ProgressDialog
        progessDialog.show()

        // Đăng nhập vào Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Đóng ProgressDialog sau khi đăng nhập hoàn thành
                progessDialog.dismiss()

                if (task.isSuccessful) {
                    // Nếu đăng nhập thành công, chuyển đến MainActivity và kết thúc activity hiện tại
                    notificationSuccess("Đăng nhập thành công!")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    // Nếu đăng nhập thất bại, hiển thị thông báo lỗi
                    notificationError("Đăng nhập thất bại!")
                }
            }
    }

    // Hàm thông báo đăng nhập thành công
    override fun notificationSuccess(messageSuccess: String) {
        Toast.makeText(this, messageSuccess, Toast.LENGTH_SHORT).show()
    }

    // Hàm thông báo đăng nhập thất bại
    override fun notificationError(messageError: String) {
        Toast.makeText(this, messageError, Toast.LENGTH_SHORT).show()
    }
}
