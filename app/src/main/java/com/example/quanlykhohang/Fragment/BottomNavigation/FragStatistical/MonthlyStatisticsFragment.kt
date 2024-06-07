package com.example.quanlykhohang.Fragment.BottomNavigation.FragStatistical

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentMonthlyStatisticsBinding

class MonthlyStatisticsFragment : Fragment() {
    private lateinit var binding: FragmentMonthlyStatisticsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_monthly_statistics, container, false)
        binding = FragmentMonthlyStatisticsBinding.bind(view)
        setHasOptionsMenu(true)
        initActionBar()

        return view
    }

    private fun initActionBar() {
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        checkNotNull(actionBar) { "ActionBar should not be null" }
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            requireActivity().supportFragmentManager.popBackStack()
            closeMenu()
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            checkNotNull(actionBar) { "ActionBar should not be null" }
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun closeMenu() {
        (requireActivity() as MenuControl).closeMenu()
    }
}