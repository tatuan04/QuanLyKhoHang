package com.example.quanlykhohang.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlykhohang.Activity.MainActivity
import com.example.quanlykhohang.Activity.SignInActivity
import com.example.quanlykhohang.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        Handler().postDelayed({ nextActivity() }, 2000)
    }

    private fun nextActivity() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}