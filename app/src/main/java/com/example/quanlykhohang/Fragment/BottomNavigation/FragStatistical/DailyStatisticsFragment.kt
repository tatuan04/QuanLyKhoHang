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
import com.example.quanlykhohang.Adapter.Statistics.DailyStatisticsAdapter
import com.example.quanlykhohang.Interface.MenuControl
import com.example.quanlykhohang.Model.Statistical
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentDailyStatisticsBinding
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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.*
class DailyStatisticsFragment : Fragment() {
    private lateinit var binding: FragmentDailyStatisticsBinding
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDailyStatisticsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        initActionBar()
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        database = FirebaseDatabase.getInstance().reference
        retrieveDailyStatsWithinLast7Days { dailyStatsList ->
            retrieveRevenueWithinLast7Days { dailyRevenues ->
                retrieveTotalRevenueWithinLast7Days { totalRevenue ->
                    val barEntries1 = ArrayList<BarEntry>()
                    val barEntries2 = ArrayList<BarEntry>()
                    val barEntries3 = ArrayList<BarEntry>()
                    val barEntries4 = ArrayList<BarEntry>()

                    for (i in 0 until minOf(7, dailyRevenues.size)) {
                        val dailyStat = dailyRevenues[i]
                        val dailyStats = dailyStatsList[i]
                        val totalIncoming = dailyStat.tongDoanhThu
                        val totalIncomingg = dailyStats.tongVao
                        val totalOutgoing = dailyStats.tongRa
                        barEntries1.add(BarEntry(i + 1f, totalIncomingg.toFloat()))
                        barEntries2.add(BarEntry(i + 1f, totalOutgoing.toFloat()))
                        barEntries3.add(BarEntry(i + 1f, totalIncoming.toFloat()))
                        barEntries4.add(BarEntry(i + 1f, totalRevenue.toFloat()))
                    }

                    val barDataSet1 = BarDataSet(barEntries1, "Nhập kho")
                    val barDataSet2 = BarDataSet(barEntries2, "Xuất kho")
                    val barDataSet3 = BarDataSet(barEntries3, "Tổng DT hằng ngày")
                    val barDataSet4 = BarDataSet(barEntries4, "Tổng doanh thu cả tuần")
                    barDataSet1.color = Color.RED
                    barDataSet2.color = Color.BLUE
                    barDataSet3.color = Color.RED
                    barDataSet4.color = Color.BLUE

                    val barData = BarData(barDataSet1, barDataSet2)
                    val barDatat = BarData(barDataSet3, barDataSet4)
                    binding.barChart.data = barDatat
                    binding.lineChart.data = barData

                    val days = Array(7) { "" }
                    for (i in 0 until 7) {
                        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                        days[i] = dateFormat.format(calendar.time)
                        calendar.add(Calendar.DAY_OF_MONTH, -1)
                    }
                    val day = arrayOf(days[0], days[1], days[2], days[3], days[4], days[5], days[6])

                    val xAxis = binding.barChart.xAxis
                    val xAxiss = binding.lineChart.xAxis

                    xAxiss.valueFormatter = IndexAxisValueFormatter(day)
                    xAxis.valueFormatter = IndexAxisValueFormatter(day)
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
                    binding.barChart.setVisibleXRangeMaximum(7f)
                    binding.lineChart.setVisibleXRangeMaximum(7f)

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
        binding.btnTatCaNgay.setOnClickListener { lifecycleScope.launch {
            loadData()
        } }

        binding.btnkhoang.setOnClickListener {
            try {
                val ngaybd = binding.edtngaybd.text.toString()
                val ngaykt = binding.edtngaykt.text.toString()
                if (ngaybd.isEmpty() || ngaykt.isEmpty()) {
                    binding.edtngaybd.error = "Không được để trống"
                    binding.edtngaykt.error = "Không được để trống"
                    Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (ngaybd > ngaykt) {
                    binding.edtngaybd.error = "Ngày bắt đầu phải nhỏ hơn ngày kết thúc"
                    binding.edtngaykt.error = "Ngày kết thúc phải lớn hơn ngày bắt đầu"
                    Toast.makeText(requireContext(), "Ngày bắt đầu phải nhỏ hơn ngày kết thúc", Toast.LENGTH_SHORT).show()
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
                val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                    val formattedYear = year.toString()
                    val formattedMonth = if (month < 9) "0${month + 1}" else (month + 1).toString()
                    val formattedDay = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
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

    suspend fun getDailyStatsWithinLast7Days(): List<Statistical> {
        val dailyStatsList = mutableListOf<Statistical>()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        for (i in 0 until 7) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = sdf.format(calendar.time)

            val dailyStat = getDailyStatForDate(dateStr)
            dailyStatsList.add(dailyStat)
        }

        return dailyStatsList
    }

    suspend fun getDailyStatForDate(date: String): Statistical {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("BillDetails")
        val snapshot = reference.orderByChild("createdDate").equalTo(date).get().await()

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

        return Statistical(date, totalIncoming, totalOutgoing)
    }

    suspend fun getBillStatus(billId: String): String {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Bills").child(billId.toString()).child("status")
        return reference.get().await().getValue(String::class.java) ?: ""
    }




    suspend fun getRevenueWithinLast7Days(): List<Statistical> {
        val revenueList = mutableListOf<Statistical>()
        val database = FirebaseDatabase.getInstance()
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        for (i in 0 until 7) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = sdf.format(calendar.time)

            try {
                val dailyRevenue = calculateDailyRevenue(database, dateStr)
                revenueList.add(Statistical(dailyRevenue))
            } catch (e: Exception) {
                // Handle errors
                Log.e("Error", "Error getting revenue for date: $dateStr", e)
            }
        }

        return revenueList
    }

    suspend fun calculateDailyRevenue(database: FirebaseDatabase, dateStr: String): Double {
        var dailyRevenue = 0.0
        val productsRef = database.getReference("Products")
        val billDetailsRef = database.getReference("BillDetails")
        val billRef = database.getReference("Bills")
        val billsSnapshot = billDetailsRef.orderByChild("createdDate").equalTo(dateStr).get().await()

        val billIds = mutableListOf<String>()
        for (detailData in billsSnapshot.children) {
            val billId = detailData.child("idBill").getValue(String::class.java) ?: ""
            billIds.add(billId)
        }
        billIds.forEach { billId ->
            val billData = billRef.child(billId).get().await()
            val status = billData.child("status").getValue(String::class.java)
            if (status == "1") {
                val detailsSnapshot = billDetailsRef.orderByChild("idBill").equalTo(billId).get().await()
                for (detailData in detailsSnapshot.children) {
                    val productId = detailData.child("idProduct").getValue(Int::class.java) ?: 0
                    val quantity = detailData.child("quantity").getValue(Int::class.java) ?: 0
                    val productData = productsRef.child(productId.toString()).get().await()
                    val priceXuat = detailData.child("exportPrice").getValue(Double::class.java) ?: 0.0
                    val price = productData.child("price").getValue(Double::class.java) ?: 0.0
                    dailyRevenue += (priceXuat - price) * quantity
                }
            }
        }
        return dailyRevenue
    }




    suspend fun getTotalRevenueWithinLast7Days(): Double {
        val database = FirebaseDatabase.getInstance()
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        var totalRevenue = 0.0

        for (i in 0 until 7) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = sdf.format(calendar.time)

            try {
                val dailyRevenue = calculateDailyRevenue(database, dateStr)
                totalRevenue += dailyRevenue
            } catch (e: Exception) {
                // Handle errors
                Log.e("Error", "Error getting revenue for date: $dateStr", e)
            }
        }

        return totalRevenue
    }



    fun retrieveDailyStatsWithinLast7Days(callback: (List<Statistical>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val dailyStatsList = getDailyStatsWithinLast7Days()
            withContext(Dispatchers.Main) {
                callback(dailyStatsList)
            }
        }
    }

    fun retrieveRevenueWithinLast7Days(callback: (List<Statistical>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val dailyRevenues = getRevenueWithinLast7Days()
            withContext(Dispatchers.Main) {
                callback(dailyRevenues)
            }
        }
    }
    fun retrieveTotalRevenueWithinLast7Days(callback: (Double) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val totalRevenue = getTotalRevenueWithinLast7Days()
            withContext(Dispatchers.Main) {
                callback(totalRevenue)
            }
        }
    }

    private suspend fun loadData(){
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val list = loadDataFromFirebase()
        if (list.isNotEmpty()) {
            val adapter = DailyStatisticsAdapter(list as ArrayList<Statistical> ,requireContext())
            binding.recyclerView.adapter = adapter
        }

    }
    private suspend fun loadDataFromFirebase(): List<Statistical> {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Bills")
        val snapshot = reference.orderByChild("createdDate").get().await()

        val revenueList = mutableListOf<Statistical>()
        val processedDates = HashSet<String>()

        for (data in snapshot.children) {
            val dateStr = data.child("createdDate").getValue(String::class.java) ?: continue
            val dailyRevenue = calculateDailyRevenue(database, dateStr)
            val (totalIncoming, totalOutgoing) = getRevenueForDate(dateStr)

            // Kiểm tra xem ngày đã được xử lý trước đó chưa
            if (!processedDates.contains(dateStr)) {
                revenueList.add(Statistical(dateStr, totalIncoming, totalOutgoing, dailyRevenue))
                processedDates.add(dateStr) // Thêm ngày vào HashSet để đánh dấu là đã xử lý
            }
        }

        return revenueList
    }
    private fun loadStatsForDate(date: String) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            val list = getStatsForDate(date)
            if (list.isNotEmpty()) {
                val adapter = DailyStatisticsAdapter(list as ArrayList<Statistical> ,requireContext())
                binding.recyclerView.adapter = adapter
            }
        }
    }

    private fun loadStatsBetweenDates(startDate: String, endDate: String) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            val list = getStatsBetweenDates(startDate, endDate)
            if (list.isNotEmpty()) {
                val adapter = DailyStatisticsAdapter(list as ArrayList<Statistical>, requireContext())
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
            editText.setText("$formattedDay-$formattedMonth-$formattedYear")
        }, nam, thang, dayss)

        datePickerDialog.show()
    }
    private fun getAllStats(callback: (List<Statistical>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("BillDetails")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val thongKeList = mutableListOf<Statistical>()
                for (data in snapshot.children) {
                    val thongKe = data.getValue(Statistical::class.java)
                    thongKe?.let { thongKeList.add(it) }
                }
                callback(thongKeList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu có
            }
        })
    }

    // Lấy thông tin một thống kê dựa trên ngày
    private suspend fun getStatsForDate(date: String) : List<Statistical> {
        val database = FirebaseDatabase.getInstance()
        val revenueList = mutableListOf<Statistical>()
        val dailyRevenue = calculateDailyRevenue(database, date)
        val (totalIncoming, totalOutgoing) = getRevenueForDate(date)
        revenueList.add(Statistical(date, totalIncoming, totalOutgoing, dailyRevenue))
        return revenueList
    }

    // Lấy thông tin thống kê trong khoảng thời gian
    private suspend fun getStatsBetweenDates(startDate: String, endDate: String): List<Statistical> {
        val database = FirebaseDatabase.getInstance()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val revenueList = mutableListOf<Statistical>()

        val startCalendar = Calendar.getInstance()
        startCalendar.time = sdf.parse(startDate)!!

        val endCalendar = Calendar.getInstance()
        endCalendar.time = sdf.parse(endDate)!!

        while (!startCalendar.time.after(endCalendar.time)) {
            val dateStr = sdf.format(startCalendar.time)
            val dailyRevenue = calculateDailyRevenue(database, dateStr)
            val (totalIncoming, totalOutgoing) = getRevenueForDate(dateStr)
            revenueList.add(Statistical(dateStr, totalIncoming, totalOutgoing, dailyRevenue))
            startCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return revenueList
    }

    private suspend fun getRevenueForDate(date: String): Pair<Int, Int> {
        val database = FirebaseDatabase.getInstance()
        val billDetailsRef = database.getReference("BillDetails")
        val billRef = database.getReference("Bills")


        val billsSnapshot = billRef.orderByChild("createdDate").equalTo(date).get().await()

        var totalIncoming = 0
        var totalOutgoing = 0

        for (billData in billsSnapshot.children) {
            val billId = billData.key
            val status = billData.child("status").getValue(String::class.java)


            if (status == "0" || status == "1") {
                val detailsSnapshot = billDetailsRef.orderByChild("idBill").equalTo(billId).get().await()
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