package com.example.quanlykhohang.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlykhohang.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var progessDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progessDialog = ProgressDialog(this)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAsk.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignIn.setOnClickListener {
            SignIn()
        }
    }

    private fun SignIn() {
        val email = binding.edtEmailSI.text.toString()
        val password = binding.edtPasswordSI.text.toString()
        val auth = FirebaseAuth.getInstance()
        progessDialog.show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progessDialog.dismiss()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        baseContext,
                        "Authentication success.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }


}