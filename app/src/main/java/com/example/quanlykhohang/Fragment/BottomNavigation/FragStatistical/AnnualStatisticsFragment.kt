package com.example.quanlykhohang.Fragment.BottomNavigation.FragStatistical

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentAnnualStatisticsBinding

class AnnualStatisticsFragment : Fragment() {
    private lateinit var binding: FragmentAnnualStatisticsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_annual_statistics, container, false)
        binding = FragmentAnnualStatisticsBinding.bind(view)
        return view
    }

}