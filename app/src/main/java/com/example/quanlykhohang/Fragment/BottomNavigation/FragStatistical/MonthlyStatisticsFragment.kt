package com.example.quanlykhohang.Fragment.BottomNavigation.FragStatistical

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlykhohang.Adapter.Statistics.MonthlyStatisticsAdapter
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.Model.Statistical
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentMonthlyStatisticsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date

class MonthlyStatisticsFragment : Fragment() {
    private lateinit var binding: FragmentMonthlyStatisticsBinding
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMonthlyStatisticsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        initActionBar()
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        database = FirebaseDatabase.getInstance().reference
        retrieveMonthlyStatsWithinLast6Months { monthlyStatsList ->
            retrieveRevenueWithinLast6Months { monthlyRevenues ->
                retrieveTotalRevenueWithinLast6Months { totalRevenue ->
                    val barEntries1 = ArrayList<BarEntry>()
                    val barEntries2 = ArrayList<BarEntry>()
                    val barEntries3 = ArrayList<BarEntry>()
                    val barEntries4 = ArrayList<BarEntry>()

                    for (i in 0 until minOf(6, monthlyRevenues.size)) {
                        val monthlyStat = monthlyRevenues[i]
                        val monthlyStats = monthlyStatsList[i]
                        val totalIncoming = monthlyStat.tongDoanhThu
                        val totalIncomingg = monthlyStats.tongVao
                        val totalOutgoing = monthlyStats.tongRa
                        barEntries1.add(BarEntry(i + 1f, totalIncomingg.toFloat()))
                        barEntries2.add(BarEntry(i + 1f, totalOutgoing.toFloat()))
                        barEntries3.add(BarEntry(i + 1f, totalIncoming.toFloat()))
                        barEntries4.add(BarEntry(i + 1f, totalRevenue.toFloat()))
                    }

                    val barDataSet1 = BarDataSet(barEntries1, "Nhập kho")
                    val barDataSet2 = BarDataSet(barEntries2, "Xuất kho")
                    val barDataSet3 = BarDataSet(barEntries3, "Tổng DT hằng tháng")
                    val barDataSet4 = BarDataSet(barEntries4, "Tổng doanh thu cả 6 tháng")
                    barDataSet1.color = Color.RED
                    barDataSet2.color = Color.BLUE
                    barDataSet3.color = Color.RED
                    barDataSet4.color = Color.BLUE

                    val barData = BarData(barDataSet1, barDataSet2)
                    val barDatat = BarData(barDataSet3, barDataSet4)
                    binding.barChart.data = barDatat
                    binding.lineChart.data = barData

                    val months = Array(6) { "" }
                    for (i in 0 until 6) {
                        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
                        months[i] = dateFormat.format(calendar.time)
                        calendar.add(Calendar.MONTH, -1)
                    }
                    val month =
                        arrayOf(months[0], months[1], months[2], months[3], months[4], months[5])

                    val xAxis = binding.barChart.xAxis
                    val xAxiss = binding.lineChart.xAxis

                    xAxiss.valueFormatter = IndexAxisValueFormatter(month)
                    xAxis.valueFormatter = IndexAxisValueFormatter(month)
                    xAxiss.setCenterAxisLabels(true)
                    xAxis.setCenterAxisLabels(true)
                    xAxiss.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f
                    xAxiss.granularity = 1f
                    xAxis.isGranularityEnabled = true
                    xAxiss.isGranularityEnabled = true

                    binding.lineChart.isDragEnabled = true
                    binding.barChart.isDragEnabled = true
                    binding.barChart.setVisibleXRangeMaximum(6f)
                    binding.lineChart.setVisibleXRangeMaximum(6f)

                    val barSpace = 0.06f
                    val groupSpace = 0.16f
                    barDatat.barWidth = 0.35f
                    barData.barWidth = 0.35f

                    binding.lineChart.xAxis.axisMinimum = 0f
                    binding.barChart.xAxis.axisMinimum = 0f
                    binding.barChart.axisRight.isEnabled = false
                    binding.lineChart.axisRight.isEnabled = false
                    binding.barChart.groupBars(0f, groupSpace, barSpace)
                    binding.lineChart.groupBars(0f, groupSpace, barSpace)
                    binding.lineChart.axisLeft.axisMinimum = 0f
                    binding.lineChart.invalidate()
                    binding.barChart.invalidate()
                }
            }
        }

        binding.edtngaybd.setOnClickListener { setupDatePickerDialog(binding.edtngaybd) }
        binding.edtngaykt.setOnClickListener { setupDatePickerDialog(binding.edtngaykt) }
        binding.btnTatCaNgay.setOnClickListener {
            lifecycleScope.launch {
                loadData()
            }
        }

        binding.btnkhoang.setOnClickListener {
            try {
                val ngaybd = binding.edtngaybd.text.toString()
                val ngaykt = binding.edtngaykt.text.toString()
                if (ngaybd.isEmpty() || ngaykt.isEmpty()) {
                    binding.edtngaybd.error = "Không được để trống"
                    binding.edtngaykt.error = "Không được để trống"
                    Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if (ngaybd > ngaykt) {
                    binding.edtngaybd.error = "Ngày bắt đầu phải nhỏ hơn ngày kết thúc"
                    binding.edtngaykt.error = "Ngày kết thúc phải lớn hơn ngày bắt đầu"
                    Toast.makeText(
                        requireContext(),
                        "Ngày bắt đầu phải nhỏ hơn ngày kết thúc",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                loadStatsBetweenDates(ngaybd, ngaykt)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.btn1Ngay.setOnClickListener {
            try {
                val calendar = Calendar.getInstance()
                val nam = calendar.get(Calendar.YEAR)
                val thang = calendar.get(Calendar.MONTH)
                val dayss = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog =
                    DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                        val formattedYear = year.toString()
                        val formattedMonth =
                            if (month < 9) "0${month + 1}" else (month + 1).toString()
                        val formattedDay =
                            if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                        val dates = "$formattedDay-$formattedMonth-$formattedYear"
                        loadStatsForDate(dates)
                    }, nam, thang, dayss)
                datePickerDialog.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return binding.root
    }

    suspend fun getMonthlyStatsWithinLast6Months(): List<Statistical> {
        val monthlyStatsList = mutableListOf<Statistical>()
        val sdf = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        for (i in 0 until 6) {
            calendar.time = Date()
            calendar.add(Calendar.MONTH, -i)
            val dateStr = sdf.format(calendar.time)

            val monthlyStat = getMonthlyStatForMonth(dateStr)
            monthlyStatsList.add(monthlyStat)
        }

        return monthlyStatsList
    }

    suspend fun getMonthlyStatForMonth(month: String): Statistical {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("BillDetails")
        val snapshot = reference.orderByChild("createdDate").equalTo(month).get().await()

        var totalIncoming = 0
        var totalOutgoing = 0

        for (data in snapshot.children) {
            val billId = (data.child("idBill").getValue(String::class.java) ?: "")
            val status = getBillStatus(billId)
            val quantity = data.child("quantity").getValue(Int::class.java) ?: 0
            if (status == "0") {
                totalIncoming += quantity
            } else {
                totalOutgoing += quantity
            }
        }

        return Statistical(month, totalIncoming, totalOutgoing)
    }

    suspend fun getBillStatus(billId: String): String {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Bills").child(billId.toString()).child("status")
        return reference.get().await().getValue(String::class.java) ?: ""
    }

    suspend fun getRevenueWithinLast6Months(): List<Statistical> {
        val revenueList = mutableListOf<Statistical>()
        val database = FirebaseDatabase.getInstance()
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM-yyyy", Locale.getDefault())

        for (i in 0 until 6) {
            calendar.time = Date()
            calendar.add(Calendar.MONTH, -i)
            val dateStr = sdf.format(calendar.time)

            try {
                val monthlyRevenue = calculateMonthlyRevenue(database, dateStr)
                revenueList.add(Statistical(monthlyRevenue))
            } catch (e: Exception) {
                // Handle errors
                Log.e("Error", "Error getting revenue for month: $dateStr", e)
            }
        }

        return revenueList
    }

    suspend fun calculateMonthlyRevenue(database: FirebaseDatabase, month: String): Double {
        var monthlyRevenue = 0.0
        val productsRef = database.getReference("Products")
        val billDetailsRef = database.getReference("BillDetails")
        val billRef = database.getReference("Bills")
        val billsSnapshot =
            billDetailsRef.orderByChild("createdDate").equalTo(month).get().await()

        val billIds = mutableListOf<String>()
        for (detailData in billsSnapshot.children) {
            val billId = detailData.child("idBill").getValue(String::class.java) ?: ""
            billIds.add(billId)
        }
        billIds.forEach { billId ->
            val billData = billRef.child(billId).get().await()
            val status = billData.child("status").getValue(String::class.java)
            if (status == "1") {
                val detailsSnapshot =
                    billDetailsRef.orderByChild("idBill").equalTo(billId).get().await()
                for (detailData in detailsSnapshot.children) {
                    val productId = detailData.child("idProduct").getValue(Int::class.java) ?: 0
                    val quantity = detailData.child("quantity").getValue(Int::class.java) ?: 0
                    val productData = productsRef.child(productId.toString()).get().await()
                    val priceXuat =
                        detailData.child("exportPrice").getValue(Double::class.java) ?: 0.0
                    val price = productData.child("price").getValue(Double::class.java) ?: 0.0
                    monthlyRevenue += (priceXuat - price) * quantity
                }
            }
        }
        return monthlyRevenue
    }

    suspend fun getTotalRevenueWithinLast6Months(): Double {
        val database = FirebaseDatabase.getInstance()
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        var totalRevenue = 0.0

        for (i in 0 until 6) {
            calendar.time = Date()
            calendar.add(Calendar.MONTH, -i)
            val dateStr = sdf.format(calendar.time)

            try {
                val monthlyRevenue = calculateMonthlyRevenue(database, dateStr)
                totalRevenue += monthlyRevenue
            } catch (e: Exception) {
                // Handle errors
                Log.e("Error", "Error getting revenue for month: $dateStr", e)
            }
        }

        return totalRevenue
    }

    fun retrieveMonthlyStatsWithinLast6Months(callback: (List<Statistical>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val monthlyStatsList = getMonthlyStatsWithinLast6Months()
            withContext(Dispatchers.Main) {
                callback(monthlyStatsList)
            }
        }
    }

    fun retrieveRevenueWithinLast6Months(callback: (List<Statistical>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val monthlyRevenues = getRevenueWithinLast6Months()
            withContext(Dispatchers.Main) {
                callback(monthlyRevenues)
            }
        }
    }

    fun retrieveTotalRevenueWithinLast6Months(callback: (Double) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val totalRevenue = getTotalRevenueWithinLast6Months()
            withContext(Dispatchers.Main) {
                callback(totalRevenue)
            }
        }
    }

    private suspend fun loadData() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val list = loadDataFromFirebase()
        if (list.isNotEmpty()) {
            val adapter = MonthlyStatisticsAdapter(list as ArrayList<Statistical>, requireContext())
            binding.recyclerView.adapter = adapter
        }

    }

    private suspend fun loadDataFromFirebase(): List<Statistical> {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Bills")
        val snapshot = reference.orderByChild("createdDate").get().await()

        val revenueList = mutableListOf<Statistical>()
        val processedMonths = HashSet<String>()

        for (data in snapshot.children) {
            val monthStr = data.child("createdDate").getValue(String::class.java) ?: continue
            val monthlyRevenue = calculateMonthlyRevenue(database, monthStr)
            val (totalIncoming, totalOutgoing) = getRevenueForMonth(monthStr)

            // Kiểm tra xem tháng đã được xử lý trước đó chưa
            if (!processedMonths.contains(monthStr)) {
                revenueList.add(Statistical(monthStr, totalIncoming, totalOutgoing, monthlyRevenue))
                processedMonths.add(monthStr) // Thêm tháng vào HashSet để đánh dấu là đã xử lý
            }
        }

        return revenueList
    }

    private fun loadStatsForDate(date: String) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            val list = getStatsForMonth(date)
            if (list.isNotEmpty()) {
                val adapter =
                    MonthlyStatisticsAdapter(list as ArrayList<Statistical>, requireContext())
                binding.recyclerView.adapter = adapter
            }
        }
    }

    private fun loadStatsBetweenDates(startDate: String, endDate: String) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            val list = getStatsBetweenMonths(startDate, endDate)
            if (list.isNotEmpty()) {
                val adapter =
                    MonthlyStatisticsAdapter(list as ArrayList<Statistical>, requireContext())
                binding.recyclerView.adapter = adapter
            }
        }
    }

    private fun setupDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val nam = calendar.get(Calendar.YEAR)
        val thang = calendar.get(Calendar.MONTH)
        val dayss = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val formattedYear = year.toString()
            val formattedMonth = if (month < 9) "0${month + 1}" else (month + 1).toString()
            val formattedDay = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            editText.setText("$formattedMonth-$formattedYear")
        }, nam, thang, dayss)

        datePickerDialog.show()
    }

    private fun getAllStats(callback: (List<Statistical>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("BillDetails")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val statsList = mutableListOf<Statistical>()
                for (data in snapshot.children) {
                    val stats = data.getValue(Statistical::class.java)
                    stats?.let { statsList.add(it) }
                }
                callback(statsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if any
            }
        })
    }

    // Get statistics for a specific month
    private suspend fun getStatsForMonth(month: String): List<Statistical> {
        val database = FirebaseDatabase.getInstance()
        val revenueList = mutableListOf<Statistical>()
        val monthlyRevenue = calculateMonthlyRevenue(database, month)
        val (totalIncoming, totalOutgoing) = getRevenueForMonth(month)
        revenueList.add(Statistical(month, totalIncoming, totalOutgoing, monthlyRevenue))
        return revenueList
    }

    // Get statistics within a range of months
    private suspend fun getStatsBetweenMonths(
        startMonth: String,
        endMonth: String
    ): List<Statistical> {
        val database = FirebaseDatabase.getInstance()
        val sdf = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val revenueList = mutableListOf<Statistical>()

        val startCalendar = Calendar.getInstance()
        startCalendar.time = sdf.parse(startMonth)!!

        val endCalendar = Calendar.getInstance()
        endCalendar.time = sdf.parse(endMonth)!!

        while (!startCalendar.time.after(endCalendar.time)) {
            val monthStr = sdf.format(startCalendar.time)
            val monthlyRevenue = calculateMonthlyRevenue(database, monthStr)
            val (totalIncoming, totalOutgoing) = getRevenueForMonth(monthStr)
            revenueList.add(Statistical(monthStr, totalIncoming, totalOutgoing, monthlyRevenue))
            startCalendar.add(Calendar.MONTH, 1)
        }
        return revenueList
    }

    // Get revenue details for a specific month
    private suspend fun getRevenueForMonth(month: String): Pair<Int, Int> {
        val database = FirebaseDatabase.getInstance()
        val billDetailsRef = database.getReference("BillDetails")
        val billRef = database.getReference("Bills")

        val billsSnapshot = billRef.orderByChild("createdDate").equalTo(month).get().await()

        var totalIncoming = 0
        var totalOutgoing = 0

        for (billData in billsSnapshot.children) {
            val billId = billData.key
            val status = billData.child("status").getValue(String::class.java)

            if (status == "0" || status == "1") {
                val detailsSnapshot =
                    billDetailsRef.orderByChild("idBill").equalTo(billId).get().await()
                for (detailData in detailsSnapshot.children) {
                    val quantity = detailData.child("quantity").getValue(Int::class.java) ?: 0
                    if (status == "0") {
                        totalIncoming += quantity
                    } else if (status == "1") {
                        totalOutgoing += quantity
                    }
                }
            }
        }
        return Pair(totalIncoming, totalOutgoing)
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

