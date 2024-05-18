package com.example.quanlykhohang.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.quanlykhohang.Fragment.DrawerNavigation.BlankFragment
import com.example.quanlykhohang.Fragment.DrawerNavigation.BlankFragment2
import com.example.quanlykhohang.Fragment.DrawerNavigation.BlankFragment3
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val header = binding.nav.getHeaderView(0)
        val txtTen = header.findViewById<TextView>(R.id.txtNamess)
        val fullName = header.findViewById<TextView>(R.id.txtFullNames)
        val imgAvatar = header.findViewById<ImageView>(R.id.imgAvatarr)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        }

        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        supportFragmentManager.beginTransaction().replace(R.id.fame, BlankFragment()).commit()
        binding.nav.setNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.product -> fragment = BlankFragment2()
                R.id.user -> fragment = BlankFragment3()
                R.id.dangxuat -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@MainActivity, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            fragment?.let {
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.fame, it).commit()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                binding.toolbar.title = item.title
            }
            false
        }
    }
}