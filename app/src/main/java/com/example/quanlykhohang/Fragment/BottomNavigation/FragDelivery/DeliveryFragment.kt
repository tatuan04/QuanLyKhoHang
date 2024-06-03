package com.example.quanlykhohang.Fragment.BottomNavigation.FragDelivery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentDeliveryBinding

class DeliveryFragment : Fragment() {
    private lateinit var binding: FragmentDeliveryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delivery, container, false)
        binding = FragmentDeliveryBinding.bind(view)

        return view
    }


}