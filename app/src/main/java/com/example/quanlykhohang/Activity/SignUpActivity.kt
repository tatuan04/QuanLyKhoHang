package com.example.quanlykhohang.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlykhohang.Model.User
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ActivitySignUpBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var userList = ArrayList<User>()
    private var email: String? = null
    private var password: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelSU.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignUp.setOnClickListener {
            email = binding.edtEmailSU.text.toString()
            password = binding.edtPasswordSU.text.toString()
            SignUp()
            createUser()
        }
        getUser()
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

    private fun createUser() {
        var size = 0
        var id = 0
        if (userList.isEmpty()) {
            id = 1
        } else {
            size = userList.size - 1
            id = userList[size].id!! + 1
        }
        val newUser = User(
            id,
            email,
            password,
            getString(R.string.linkAnh)
        )
        addData(newUser)
    }

    private fun addData(user: User) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        val userID = user.id.toString()
        myRef.child(userID).setValue(user)
    }

    private fun SignUp() {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email.toString(), password.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                    Toast.makeText(
                        baseContext,
                        "Authentication success.",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}