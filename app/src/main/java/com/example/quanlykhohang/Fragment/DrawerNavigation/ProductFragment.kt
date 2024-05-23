package com.example.quanlykhohang.Fragment.DrawerNavigation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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


class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding
    private val listProduct = ArrayList<Product>()
    private lateinit var adapterProduct: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)
        binding = FragmentProductBinding.bind(view)

        loadData()

        binding.fabAddProduct.setOnClickListener {
//            // Khởi tạo Fragment mới
//            val addProductFragment = AddProductFragment()
//
//            // Lấy ra FragmentManager từ activity
//            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
//
//            // Bắt đầu một giao dịch Fragment
//            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//
//            // Thay thế Fragment hiện tại bằng Fragment mới
//            fragmentTransaction.replace(R.id.fragment_product, addProductFragment)
//
//            // Thêm transaction vào back stack (nếu cần)
//            fragmentTransaction.addToBackStack(null)
//
//            // Hoàn thành giao dịch
//            fragmentTransaction.commit()
            (requireActivity()as TransferFragment).transferFragment(AddProductFragment(), "AddProductFragment")
        }
        return view
    }

    private fun loadData() {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapterProduct = ProductAdapter(listProduct)
        binding.recyclerview.adapter = adapterProduct
        feactData()
    }

    private fun feactData() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listProduct.clear()
                for (snapshot: DataSnapshot in snapshot.children) {
                    val product = snapshot.getValue(Product::class.java)
                    if (product != null) {
                        listProduct.add(product)
                        adapterProduct.setData(listProduct)
                        Log.d("TAG", "onDataChange: $product")
                    }
                }
                adapterProduct.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

}