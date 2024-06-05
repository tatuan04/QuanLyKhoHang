package com.example.quanlykhohang.Fragment.BottomNavigation.FragReceipt
// Khai báo gói, xác định vị trí của lớp AddReceiptFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentAddReceiptBinding

class AddReceiptFragment : Fragment() {

    private lateinit var binding: FragmentAddReceiptBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment_add_receipt để tạo đối tượng View từ tài nguyên XML
        val view = inflater.inflate(R.layout.fragment_add_receipt, container, false)
        // Khởi tạo binding bằng cách ràng buộc view đã được inflate
        binding = FragmentAddReceiptBinding.bind(view)

        // Thông báo rằng fragment này có một menu tùy chọn
        setHasOptionsMenu(true)
        // Lấy ActionBar từ activity đang chứa
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        // Đảm bảo rằng ActionBar không null
        requireNotNull(actionBar) { "Action bar is null" }
        // Bật nút home như một nút "up"
        actionBar.setDisplayHomeAsUpEnabled(true)
        // Đặt biểu tượng của nút home thành biểu tượng mũi tên quay lại tùy chỉnh
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        val index = intArrayOf(0)
        binding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                index[0] = i
                binding.btnAdd.text = if (index[0] > 1) "Tiếp" else "Tạo"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Không làm gì khi bắt đầu theo dõi chạm seekbar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Không làm gì khi kết thúc theo dõi chạm seekbar
            }
        })

        binding.btnAdd.setOnClickListener {

        }


        // Trả về view đã được inflate và ràng buộc để hiển thị bởi fragment
        return view

    }

    // Ghi đè phương thức onOptionsItemSelected để xử lý các lựa chọn mục menu
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Lấy ID của mục menu được chọn
        val id = item.itemId
        // Nếu mục được chọn là nút home (ID android.R.id.home)
        if (id == android.R.id.home) {
            // Pop back stack của fragment để trở về fragment trước đó
            requireActivity().supportFragmentManager.popBackStack()
            // Gọi phương thức closeMenu để đóng menu
            closeMenu()
            // Lấy lại ActionBar từ activity đang chứa
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            // Đảm bảo rằng ActionBar không null
            requireNotNull(actionBar) { "Action bar is null" }
            // Đặt lại biểu tượng của nút home thành biểu tượng menu
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
            // Trả về true để xác nhận rằng sự kiện đã được xử lý
            return true
        }
        // Nếu mục được chọn không phải là nút home, chuyển sự kiện cho thực thi của lớp cha
        return super.onOptionsItemSelected(item)

    }

    // Phương thức trợ giúp để đóng menu
    private fun closeMenu() {
        // Ép kiểu activity thành MenuControl và gọi phương thức closeMenu của nó
        (requireActivity() as MenuControl).closeMenu()
    }
}
