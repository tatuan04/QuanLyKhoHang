package com.example.quanlykhohang.Fragment.DrawerNavigation.FragProduct

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlykhohang.Adapter.ProductAdapter
import com.example.quanlykhohang.Interface.TransferFragment
import com.example.quanlykhohang.Model.Product
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentProductBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Fragment để hiển thị danh sách sản phẩm
class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding
    private val listProduct = ArrayList<Product>() // Danh sách sản phẩm
    private lateinit var adapterProduct: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)
        binding = FragmentProductBinding.bind(view)

        // Load dữ liệu và khởi tạo RecyclerView
        loadData()

        // Lấy dữ liệu từ Firebase Realtime Database và cập nhật RecyclerView
        fetchData()

        // Xử lý sự kiện khi nhấn nút "Add Product"
        binding.fabAddProduct.setOnClickListener {
            // Gọi phương thức transferFragment từ activity gốc để thay đổi Fragment
            (requireActivity() as TransferFragment).transferFragment(
                AddProductFragment(),
                "AddProductFragment"
            )
        }
        return view
    }

    // Load dữ liệu và khởi tạo RecyclerView
    private fun loadData() {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapterProduct = ProductAdapter(listProduct, requireContext())
        binding.recyclerview.adapter = adapterProduct
    }

    // Lấy dữ liệu từ Firebase Realtime Database và cập nhật RecyclerView
    private fun fetchData() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Xóa danh sách sản phẩm cũ và cập nhật với dữ liệu mới từ Firebase
                listProduct.clear()
                for (snapshot: DataSnapshot in snapshot.children) {
                    val product = snapshot.getValue(Product::class.java)
                    if (product != null) {
                        listProduct.add(product)
                        Log.d("tuan", "onDataChange: $product")
                    }
                }
                // Thông báo cho Adapter biết rằng dữ liệu đã thay đổi
                adapterProduct.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                Log.w("tuan", "Failed to read value.", error.toException())
            }
        })
    }

    fun isExpanded(): Boolean {
        return isExpanded()
    }

}
