package com.example.quanlykhohang.Fragment.BottomNavigation.FragDelivery

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlykhohang.Adapter.Delivery.DeliveryBillDetailAdapter
import com.example.quanlykhohang.Adapter.Receipt.ReceiptBillDetailAdapter
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.Model.Bill
import com.example.quanlykhohang.Model.BillDetail
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentDeliveryDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeliveryDetailFragment : Fragment() {
    private lateinit var binding: FragmentDeliveryDetailBinding
    private lateinit var adapter: DeliveryBillDetailAdapter
    private val listBillDetail = ArrayList<BillDetail>()
    private val listBill = ArrayList<Bill>()
    private lateinit var args: Bundle
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delivery_detail, container, false)
        binding = FragmentDeliveryDetailBinding.bind(view)

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

        args = requireArguments()
        val idBill = args.getInt("id")

        loadData()
        featchData(idBill)

        return view
    }

    private fun loadData() {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = DeliveryBillDetailAdapter(listBillDetail, requireContext())
        binding.recyclerview.adapter = adapter
    }

    private fun featchData(idBill: Int) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Bills")
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                listBill.clear()
                for (snapshot: DataSnapshot in snapshot.children) {
                    val bill = snapshot.getValue(Bill::class.java)
                    if (bill != null) {
                        listBill.add(bill)
                        Log.d("tuan", "onDataChange: $bill")
                    }
                }
                getListBillDetail(idBill)
            }

            override fun onCancelled(error: DatabaseError) {

                Log.w("tuan", "Failed to read value.", error.toException())
            }
        })
    }

    private fun getListBillDetail(idBill: Int) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("BillDetails")
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                listBillDetail.clear()
                for (snapshot: DataSnapshot in snapshot.children) {
                    val billDetail = snapshot.getValue(BillDetail::class.java)
                    if (billDetail?.idBill == idBill) {
                        listBillDetail.add(billDetail)
                        Log.d("tuan", "onDataChange: $billDetail")
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("tuan", "Failed to read value.", error.toException())
            }
        })
    }

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