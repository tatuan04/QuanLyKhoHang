package com.example.quanlykhohang.Fragment.DrawerNavigation.FragUser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlykhohang.Adapter.User.UserAdpater
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.Model.User
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentListUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListUserFragment : Fragment() {
    private lateinit var binding: FragmentListUserBinding
    private lateinit var listUser: ArrayList<User>
    private lateinit var adapter: UserAdpater

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_user, container, false)
        binding = FragmentListUserBinding.bind(view)
        listUser = ArrayList()

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

        loadData()
        feachData()
        return view
    }

    private fun loadData() {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserAdpater(listUser, requireContext())
        binding.recyclerview.adapter = adapter
    }

    private fun feachData() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listUser.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        listUser.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
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