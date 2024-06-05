package com.example.quanlykhohang.Fragment.BottomNavigation.FragReceipt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlykhohang.Adapter.Receipt.ReceiptBillAdapter
import com.example.quanlykhohang.Interface.TransferFragment
import com.example.quanlykhohang.Model.Bill
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentReceiptBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReceiptFragment : Fragment() {
    private lateinit var binding: FragmentReceiptBinding
    private val listBill = ArrayList<Bill>()
    private lateinit var adapter: ReceiptBillAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_receipt, container, false)
        binding = FragmentReceiptBinding.bind(view)

        setIcon()
        loadData()
        feachData()

        binding.fabAddReceipt.setOnClickListener {
            (requireActivity() as TransferFragment).transferFragment(
                AddReceiptFragment(),
                "AddReceiptFragment"
            )
        }
        return view
    }

    private fun setIcon() {
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
    }

    private fun loadData() {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReceiptBillAdapter(listBill, requireContext())
        binding.recyclerview.adapter = adapter
    }

    private fun feachData() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Bills")
        myRef.orderByChild("status").equalTo("0")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    listBill.clear()
                    for (postSnapshot in dataSnapshot.children) {
                        val bill = postSnapshot.getValue(Bill::class.java)
                        if (bill != null) {
                            listBill.add(bill)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    Log.d("TAG", listBill.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("TAG", "loadPost:onCancelled", error.toException())
                }
            })
    }
}