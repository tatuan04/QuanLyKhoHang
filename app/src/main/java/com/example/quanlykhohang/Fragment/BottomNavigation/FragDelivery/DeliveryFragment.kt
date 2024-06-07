package com.example.quanlykhohang.Fragment.BottomNavigation.FragDelivery

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlykhohang.Adapter.Delivery.DeliveryBillAdapter
import com.example.quanlykhohang.Adapter.Receipt.ReceiptBillAdapter
import com.example.quanlykhohang.Interface.TransferFragment
import com.example.quanlykhohang.Model.Bill
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentDeliveryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeliveryFragment : Fragment() {
    private lateinit var binding: FragmentDeliveryBinding
    private val listBill = ArrayList<Bill>()
    private lateinit var adapter: DeliveryBillAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delivery, container, false)
        binding = FragmentDeliveryBinding.bind(view)

        setIcon()
        loadData()
        feachData()

        binding.fabAddDelivery.setOnClickListener {
            (requireActivity() as TransferFragment).transferFragment(
                AddDeliveryFragment(),
                "AddDeliveryFragment"
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
        adapter = DeliveryBillAdapter(listBill, requireContext())
        binding.recyclerview.adapter = adapter
    }

    private fun feachData() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Bills")
        myRef.orderByChild("status").equalTo("1")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    listBill.clear()
                    for (ds in snapshot.children) {
                        val bill = ds.getValue(Bill::class.java)
                        listBill.add(bill!!)
                    }
                    adapter.notifyDataSetChanged()
                    Log.d("TAG", listBill.toString())

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
    }

}