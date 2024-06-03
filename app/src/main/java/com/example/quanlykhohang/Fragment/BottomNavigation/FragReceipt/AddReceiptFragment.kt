package com.example.quanlykhohang.Fragment.BottomNavigation.FragReceipt
// Khai báo gói, xác định vị trí của lớp AddReceiptFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentAddReceiptBinding
// Các câu lệnh import cần thiết cho việc sử dụng các lớp và giao diện từ Android framework và các gói của dự án

class AddReceiptFragment : Fragment() {
    // Khai báo lớp AddReceiptFragment kế thừa từ Fragment

    private lateinit var binding: FragmentAddReceiptBinding
    // Khai báo biến binding sẽ được khởi tạo muộn, sử dụng để ràng buộc view

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Ghi đè phương thức onCreateView để tạo giao diện người dùng cho fragment

        val view = inflater.inflate(R.layout.fragment_add_receipt, container, false)
        // Inflate layout fragment_add_receipt để tạo đối tượng View từ tài nguyên XML

        binding = FragmentAddReceiptBinding.bind(view)
        // Khởi tạo binding bằng cách ràng buộc view đã được inflate

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

        return view
        // Trả về view đã được inflate và ràng buộc để hiển thị bởi fragment
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
