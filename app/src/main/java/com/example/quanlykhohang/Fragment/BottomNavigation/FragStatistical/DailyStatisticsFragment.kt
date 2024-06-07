package com.example.quanlykhohang.Fragment.BottomNavigation.FragStatistical

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlykhohang.Adapter.Statistics.DailyStatisticsAdapter
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.Model.Statistical
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentDailyStatisticsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DailyStatisticsFragment : Fragment() {
    private lateinit var binding: FragmentDailyStatisticsBinding
    private val listStatistical = ArrayList<Statistical>()
    private lateinit var adapter: DailyStatisticsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_daily_statistics, container, false)
        binding = FragmentDailyStatisticsBinding.bind(view)
        adapter = DailyStatisticsAdapter(listStatistical, requireContext())
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

    @Deprecated("Deprecated in Java")
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