package com.example.quanlykhohang.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.quanlykhohang.Fragment.BottomNavigation.FragDelivery.DeliveryFragment
import com.example.quanlykhohang.Fragment.BottomNavigation.FragReceipt.ReceiptFragment
import com.example.quanlykhohang.Fragment.BottomNavigation.FragReport.ReportFragment
import com.example.quanlykhohang.Fragment.DrawerNavigation.FragProduct.ProductFragment
import com.example.quanlykhohang.Fragment.DrawerNavigation.FragUser.UserFragment
import com.example.quanlykhohang.Interface.FragmentInteractionListener
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.Interface.TransferFragment
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), TransferFragment, MenuControl, FragmentInteractionListener {
    // Khai báo biến binding để liên kết với layout ActivityMainBinding
    private lateinit var binding: ActivityMainBinding

    // onCreate được gọi khi Activity được tạo ra
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout và gán biến binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy ra các thành phần trong header của Navigation Drawer
        val header = binding.nav.getHeaderView(0)
        val txtTen = header.findViewById<TextView>(R.id.txtNamess)
        val fullName = header.findViewById<TextView>(R.id.txtFullNames)
        val imgAvatar = header.findViewById<ImageView>(R.id.imgAvatarr)

        // Lấy thông tin người dùng từ Firebase Realtime Database
        val currentUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("Users")

        // Truy vấn người dùng dựa trên email của người dùng hiện tại
        userRef.orderByChild("email").equalTo(currentUser?.email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Duyệt qua các userSnapshot để lấy thông tin người dùng
                        for (userSnapshot in snapshot.children) {
                            val email = userSnapshot.child("email").getValue(String::class.java)
                            val userType = userSnapshot.child("userType").getValue(String::class.java)
                            val avatar = userSnapshot.child("avatar").getValue(String::class.java)

                            // Hiển thị thông tin người dùng trên header của Navigation Drawer
                            txtTen.text = email
                            fullName.text = userType
                            if (!avatar.isNullOrEmpty()) {
                                Glide.with(this@MainActivity)
                                    .load(avatar)
                                    .into(imgAvatar)
                            }
                        }
                    } else {
                        Log.e("MainActivity", "Snapshot does not exist")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý khi có lỗi xảy ra khi truy xuất dữ liệu từ Firebase
                    Toast.makeText(
                        this@MainActivity,
                        "Lỗi khi tải thông tin người dùng",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("MainActivity", "DatabaseError: ${error.message}")
                }
            })

        // Thiết lập toolbar là action bar của activity
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)

        // Thiết lập fragment mặc định khi activity khởi động
        supportFragmentManager.beginTransaction().replace(R.id.fame, ReceiptFragment()).commit()

        // Xử lý sự kiện khi chọn một item trong Navigation Drawer
        binding.nav.setNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.product -> fragment = ProductFragment()
                R.id.user -> fragment = UserFragment()
                R.id.dangxuat -> {
                    // Đăng xuất người dùng và chuyển sang màn hình đăng nhập
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@MainActivity, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            // Thay thế fragment và đóng Navigation Drawer
            if (fragment != null) {
                // Lấy FragmentManager từ Activity
                val fragmentManager = supportFragmentManager
                // Bắt đầu một giao dịch Fragment
                val transaction = fragmentManager.beginTransaction()
                // Thay thế Fragment hiện tại bằng Fragment mới
                transaction.replace(R.id.fame, fragment)
                // Cam kết giao dịch, thực hiện các thay đổi
                transaction.commit()
                // Đóng Navigation Drawer
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                // Đặt tiêu đề của Toolbar theo tiêu đề của mục đã chọn trong Navigation Drawer
                binding.toolbar.title = item.title
            }
            false
        }

        // Xử lý sự kiện khi chọn một item trong Bottom Navigation
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.baocao -> ReportFragment()
                R.id.phieunhap -> ReceiptFragment()
                else -> DeliveryFragment()
            }
            // Thay thế fragment và đóng Bottom Navigation
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fame, fragment)
                .commit()
            binding.toolbar.title = item.title
            binding.drawerLayout.close()
            true
        }
    }

    // Chuyển đổi fragment và thêm vào back stack
    override fun transferFragment(fragment: Fragment, name: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fame, fragment)
            .addToBackStack(name)
            .commit()
    }

    // Xử lý sự kiện khi nhấn vào nút menu trên toolbar để mở Navigation Drawer
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    // Đóng Navigation Drawer
    override fun closeMenu() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    // Xử lý sự kiện khi nhấn nút back trong Fragment
    override fun onFragmentBackPressed() {
        super.onBackPressed()
    }

    // Xử lý sự kiện khi Fragment yêu cầu xử lý sự kiện chạm vào màn hình
    override fun onFragmentDispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            // ...
        }
        return super.dispatchTouchEvent(ev)
    }
}
