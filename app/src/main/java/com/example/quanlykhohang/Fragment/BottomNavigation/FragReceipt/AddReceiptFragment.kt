import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.Model.Bill
import com.example.quanlykhohang.Model.BillDetail
import com.example.quanlykhohang.Model.Product
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentAddReceiptBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AddReceiptFragment : Fragment() {

    // Khai báo các biến và danh sách dữ liệu cần thiết
    private lateinit var binding: FragmentAddReceiptBinding
    private val listProduct = ArrayList<Product>()
    private val listBill = ArrayList<Bill>()
    private val listBillDetail = ArrayList<BillDetail>()
    private var idBill = "0"
    private var idUser: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gắn giao diện cho Fragment
        binding = FragmentAddReceiptBinding.inflate(inflater, container, false)

        // Khởi tạo giao diện và thiết lập ActionBar
        initViews()
        setupActionBar()

        // Lấy dữ liệu từ Firebase
        fetchData()

        // Xử lý sự kiện khi người dùng nhấn vào nút Thêm
        binding.btnAdd.setOnClickListener { handleAddButtonClick() }

        return binding.root
    }

    // Khởi tạo các thành phần của giao diện
    private fun initViews() {
        binding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.btnAdd.text = if (progress > 1) "Tiếp" else "Tạo"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    // Thiết lập ActionBar với nút back
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

    // Tải danh sách sản phẩm từ Firebase
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

    // Xử lý sự kiện khi người dùng nhấn vào nút Thêm
    private fun handleAddButtonClick() {
        val progress = binding.seekBar2.progress
        if (progress > 0) {
            insertSp()
            binding.spnTenSP.setSelection(0)
            binding.txtSoLuong.setText("")
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

    // Thêm sản phẩm vào kho và tạo chi tiết hóa đơn
    private fun insertSp() {
        if (isAdded) {
            val selectedItem = binding.spnTenSP.selectedItem as? Map<String, Any> ?: return
            val maSP = selectedItem["maSP"] as? Int ?: return
            val gia = selectedItem["gia"] as? Int ?: return
            val soL = binding.txtSoLuong.text.toString().trim()

            try {
                val soLuongSp = soL.toInt()
                val tong = soLuongSp * (selectedItem["soLuong"] as? Int ?: return)
                updateProductBill(tong, maSP, gia, selectedItem)
                addBillDetail(maSP, idBill, soLuongSp, gia, selectedItem)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("AddReceiptFragment", "Fragment not attached to a context")
        }
    }

    // Cập nhật dữ liệu sản phẩm trong kho
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

    // Thêm chi tiết hóa đơn
    private fun addBillDetail(
        maSP: Int,
        idBill: String,
        soLuongSp: Int,
        gia: Int,
        selectedItem: Map<String, Any>
    ) {
        try {
            // Lấy ngày hiện tại
            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            // Tạo đối tượng BillDetail
            val billDetail = BillDetail(
                maSP,
                idBill,
                selectedItem["nameSP"] as? String ?: "",
                soLuongSp,
                gia,
                currentDate
            )
            // Tính kích thước danh sách chi tiết hóa đơn và thêm vào Firebase
            val size = listBillDetail.size + 1
            FirebaseDatabase.getInstance().reference.child("BillDetails").child(size.toString())
                .setValue(billDetail)
        } catch (e: Exception) {
            Log.e("AddReceiptFragment", "Error adding bill detail: ${e.message}")
        }
    }

    // Thêm hóa đơn
    private fun insertHoaDon() {
        try {
            // Lấy ngày hiện tại
            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            // Tạo ID hóa đơn mới
            val id = if (listBill.isEmpty()) 1 else listBill.last().id + 1
            // Tạo đối tượng Bill
            val bill = Bill(id, "0", idUser, currentDate, "")
            // Thêm hóa đơn vào Firebase
            FirebaseDatabase.getInstance().reference.child("Bills").child(id.toString())
                .setValue(bill)
        } catch (e: Exception) {
            Log.e("AddReceiptFragment", "Error: ${e.message}")
        }
    }

    // Lấy ID của người dùng hiện tại
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

    // Lấy ID của hóa đơn cuối cùng và thiết lập ID mới
    private fun getIdBill() {
        FirebaseDatabase.getInstance().reference.child("Bills")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!isAdded) return

                    listBill.clear()
                    snapshot.children.mapNotNullTo(listBill) { it.getValue(Bill::class.java) }
                    idBill =
                        if (listBill.isEmpty()) 1.toString() else (listBill.last().id + 1).toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("AddReceiptFragment", "Failed to get bill ID", error.toException())
                }
            })
    }

    // Lấy danh sách chi tiết hóa đơn
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

    // Xử lý sự kiện khi nhấn nút back trên ActionBar
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

    // Đóng menu
    private fun closeMenu() {
        (requireActivity() as? MenuControl)?.closeMenu()
    }
}