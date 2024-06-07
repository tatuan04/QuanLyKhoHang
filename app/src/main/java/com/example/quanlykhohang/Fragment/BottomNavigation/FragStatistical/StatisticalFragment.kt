package com.example.quanlykhohang.Fragment.BottomNavigation.FragStatistical

import AddReceiptFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quanlykhohang.Interface.TransferFragment
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentStatisticalBinding

class StatisticalFragment : Fragment() {
    private lateinit var binding: FragmentStatisticalBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_statistical, container, false)
        binding = FragmentStatisticalBinding.bind(view)
        binding.ngay.setOnClickListener {
            (requireActivity() as TransferFragment).transferFragment(
                DailyStatisticsFragment(),
                "DailyStatisticsFragment"
            )
        }
        binding.thang.setOnClickListener {
            (requireActivity() as TransferFragment).transferFragment(
                MonthlyStatisticsFragment(),
                "MonthlyStatisticsFragment"
            )
        }
        binding.nam.setOnClickListener {
            (requireActivity() as TransferFragment).transferFragment(
                AnnualStatisticsFragment(),
                "AnnualStatisticsFragment"
            )
        }
        return view
    }

}