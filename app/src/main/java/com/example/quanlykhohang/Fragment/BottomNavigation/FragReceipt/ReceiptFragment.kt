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

        loadData()
        setIcon()
        feachData()

        binding.fabAddReceipt.setOnClickListener {
            (requireActivity() as TransferFragment).transferFragment(AddReceiptFragment(), "AddReceiptFragment")
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
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listBill.clear()
                for (snapshot: DataSnapshot in snapshot.children) {
                    val bill = snapshot.getValue(Bill::class.java)
                    if (bill != null) {
                        listBill.add(bill)
                        Log.d("tuan", "onDataChange: $bill")
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

                Log.w("tuan", "Failed to read value.", error.toException())
            }
        })
    }
}