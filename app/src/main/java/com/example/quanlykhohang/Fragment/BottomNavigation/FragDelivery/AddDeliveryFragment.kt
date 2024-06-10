package com.example.quanlykhohang.Fragment.BottomNavigation.FragDelivery

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.Model.Bill
import com.example.quanlykhohang.Model.BillDetail
import com.example.quanlykhohang.Model.Product
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentAddDeliveryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class AddDeliveryFragment : Fragment() {
    // Khai báo biến
    private lateinit var binding: FragmentAddDeliveryBinding
    private val listProduct = ArrayList<Product>()
    private val listBill = ArrayList<Bill>()
    private val listBillDetail = ArrayList<BillDetail>()
    private var idBill = "0"
    private var idUser: String? = null

    // Khởi tạo Fragment và các thành phần giao diện
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_delivery, container, false)
        binding = FragmentAddDeliveryBinding.bind(view)

        // Khởi tạo các view và thiết lập ActionBar
        initViews()
        setupActionBar()
        fetchData()

        // Xử lý sự kiện khi nhấn nút Thêm
        binding.btnAdd.setOnClickListener { handleAddButtonClick() }

        // Xử lý sự kiện khi chọn sản phẩm trong Spinner
        binding.spnTenSP.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Lấy số lượng sản phẩm còn lại và hiển thị lên giao diện
                val HMMSP = binding.spnTenSP.selectedItem as HashMap<String, Any>?
                val itt = HMMSP?.get("soLuong") as? Int
                binding.txtSoLuongConLai.text = itt?.toString() ?: ""
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Xử lý khi không có mục nào được chọn
            }
        }
        return view
    }

    // Khởi tạo các thành phần giao diện
    private fun initViews() {
        binding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Cập nhật nội dung nút Thêm dựa trên giá trị của SeekBar
                binding.btnAdd.text = if (progress > 1) "Tiếp" else "Tạo"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Không cần thực hiện hành động khi chạm vào SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Không cần thực hiện hành động khi nhả SeekBar
            }
        })
    }

    // Thiết lập ActionBar
    private fun setupActionBar() {
        setHasOptionsMenu(true)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
    }

    // Lấy dữ liệu từ Firebase
    private fun fetchData() {
        getIdUser()
        getIdBill()
        getIdBillDetail()
        loadProducts()
    }

    // Lấy danh sách sản phẩm từ Firebase
    private fun loadProducts() {
        val database = FirebaseDatabase.getInstance().reference.child("Products")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return

                listProduct.clear()
                val listData = mutableListOf<Map<String, Any>>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        listProduct.add(it)
                        listData.add(it.toMap())
                    }
                }
                setupSpinner(listData)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("AddReceiptFragment", "Failed to read value.", error.toException())
            }
        })
    }

    // Chuyển đổi đối tượng Product thành Map<String, Any>
    private fun Product.toMap(): Map<String, Any> {
        return mapOf(
            "maSP" to id,
            "nameSP" to (name ?: ""),
            "anhSP" to (photo ?: ""),
            "idUser" to (userID ?: ""),
            "gia" to price,
            "soLuong" to quantity
        )
    }

    // Thiết lập Spinner cho danh sách sản phẩm
    private fun setupSpinner(data: List<Map<String, Any>>) {
        val adapter = SimpleAdapter(
            requireContext(),
            data,
            android.R.layout.simple_list_item_1,
            arrayOf("nameSP"),
            intArrayOf(android.R.id.text1)
        )
        binding.spnTenSP.adapter = adapter
    }

    // Xử lý khi nhấn nút Thêm
    private fun handleAddButtonClick() {
        val progress = binding.seekBar2.progress
        if (progress > 0) {
            insertSp()
            binding.spnTenSP.setSelection(0)
            binding.txtSoLuong.setText("")
            binding.txtGiaXuat.setText("")
            binding.seekBar2.progress = progress - 1
            if (progress == 1) {
                insertHoaDon()
                parentFragmentManager.popBackStack()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Vui lòng chọn số sản phẩm muốn thêm lớn hơn 0",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Thêm sản phẩm vào hóa đơn
    private fun insertSp() {
        if (isAdded) {
            val selectedItem = binding.spnTenSP.selectedItem as? Map<String, Any> ?: return
            val maSP = selectedItem["maSP"] as? Int ?: return
            val gia = selectedItem["gia"] as? Int ?: return
            val giaXuat = binding.txtGiaXuat.text.toString().trim()
            val soL = binding.txtSoLuong.text.toString().trim()

            try {
                val soLuongSp = soL.toInt()
                val tong = soLuongSp * (selectedItem["soLuong"] as? Int ?: return)
                val giaXuat = giaXuat.toInt()
                updateProductBill(tong, maSP, gia, selectedItem)
                addBillDetail(maSP, idBill, soLuongSp, gia, giaXuat, selectedItem)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("AddReceiptFragment", "Fragment not attached to a context")
        }
    }

    // Cập nhật thông tin sản phẩm trong hóa đơn
    private fun updateProductBill(tong: Int, maSP: Int, gia: Int, selectedItem: Map<String, Any>) {
        try {
            val product = Product(
                maSP,
                selectedItem["nameSP"] as? String ?: "",
                tong,
                gia,
                selectedItem["anhSP"] as? String ?: "",
                "0",
                selectedItem["idUser"] as? String ?: ""
            )
            FirebaseDatabase.getInstance().reference.child("Products").child(maSP.toString())
                .setValue(product)
        } catch (e: Exception) {
            Log.e("AddReceiptFragment", "Error updating product bill: ${e.message}")
        }
    }

    // Thêm chi tiết hóa đơn vào cơ sở dữ liệu
    private fun addBillDetail(
        maSP: Int,
        idBill: String,
        soLuongSp: Int,
        gia: Int,
        giaXuat: Int,
        selectedItem: Map<String, Any>
    ) {
        try {
            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val billDetail = BillDetail(
                maSP,
                idBill,
                selectedItem["nameSP"] as? String ?: "",
                soLuongSp,
                gia,
                giaXuat,
                currentDate
            )
            val size = listBillDetail.size + 1
            FirebaseDatabase.getInstance().reference.child("BillDetails").child(size.toString())
                .setValue(billDetail)
        } catch (e: Exception) {
            Log.e("AddReceiptFragment", "Error adding bill detail: ${e.message}")
        }
    }

    // Thêm hóa đơn vào cơ sở dữ liệu
    private fun insertHoaDon() {
        try {
            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val id = if (listBill.isEmpty()) 1 else listBill.last().id + 1

            val bill = Bill(id, "1", idUser, currentDate, "")
            FirebaseDatabase.getInstance().reference.child("Bills").child(id.toString())
                .setValue(bill)
        } catch (e: Exception) {
            Log.e("AddReceiptFragment", "Error: ${e.message}")
        }
    }

    // Lấy ID người dùng hiện tại từ Firebase
    private fun getIdUser() {
        FirebaseAuth.getInstance().currentUser?.email?.let { email ->
            FirebaseDatabase.getInstance().reference.child("Users")
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.firstOrNull()?.let {
                            idUser = it.child("userType").getValue(String::class.java)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("AddReceiptFragment", "Failed to get user ID", error.toException())
                    }
                })
        }
    }

    // Lấy ID hóa đơn cuối cùng từ Firebase
    private fun getIdBill() {
        FirebaseDatabase.getInstance().reference.child("Bills")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!isAdded) return

                    listBill.clear()
                    snapshot.children.mapNotNullTo(listBill) { it.getValue(Bill::class.java) }
                    idBill = if (listBill.isEmpty()) 1.toString() else (listBill.last().id + 1).toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("AddReceiptFragment", "Failed to get bill ID", error.toException())
                }
            })
    }

    // Lấy danh sách chi tiết hóa đơn từ Firebase
    private fun getIdBillDetail() {
        FirebaseDatabase.getInstance().reference.child("BillDetails")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!isAdded) return

                    listBillDetail.clear()
                    snapshot.children.mapNotNullTo(listBillDetail) { it.getValue(BillDetail::class.java) }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("AddReceiptFragment", "Failed to get bill details", error.toException())
                }
            })
    }

    // Xử lý sự kiện khi chọn mục trong ActionBar
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            requireActivity().supportFragmentManager.popBackStack()
            closeMenu()
            (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    // Đóng menu nếu có
    private fun closeMenu() {
        (requireActivity() as? MenuControl)?.closeMenu()
    }
}