package com.example.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.Product
import com.example.data.local.entity.SaleOrder
import com.example.data.repository.HardwareRepository
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.toBengaliNumerals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HomeSummaryMetrics(
    val totalSales: Double = 0.0,
    val ordersCount: Int = 0,
    val itemsSoldCount: Int = 0,
    val totalProfit: Double = 0.0
)

data class HomeUiState(
    val metrics: HomeSummaryMetrics = HomeSummaryMetrics(),
    val customerBaki: Double = 0.0,
    val lowStockList: List<Product> = emptyList(),
    val recentSalesList: List<SaleOrder> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = HardwareRepository(
        database.productDao(),
        database.saleOrderDao(),
        database.categoryDao(),
        database.purchaseDao(),
        database.customerDao(),
        database.supplierDao(),
        database.supplierPaymentDao()
    )

    val lowStockProducts: StateFlow<List<Product>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSales: StateFlow<List<SaleOrder>> = repository.recentSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerBaki: StateFlow<Double> = repository.totalUnpaidBaki
        .combine(MutableStateFlow(0.0)) { baki, _ -> baki ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todaySales: StateFlow<List<SaleOrder>> = repository.getTodaySales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMetrics: StateFlow<HomeSummaryMetrics> = todaySales
        .combine(lowStockProducts) { sales, _ ->
            var salesTotal = 0.0
            var itemsCount = 0
            var profitTotal = 0.0
            for (sale in sales) {
                salesTotal += sale.totalAmount
                itemsCount += sale.totalItemsCount
                profitTotal += (sale.totalAmount - sale.totalCost)
            }
            HomeSummaryMetrics(
                totalSales = salesTotal,
                ordersCount = sales.size,
                itemsSoldCount = itemsCount,
                totalProfit = profitTotal
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeSummaryMetrics())

    init {
        viewModelScope.launch {
            repository.seedInitialHardwareDataIfNeeded()
        }
    }

    fun getFormattedDate(language: AppLanguage): String {
        val now = Date()
        return if (language == AppLanguage.BANGLA) {
            val dayName = SimpleDateFormat("EEEE", Locale.US).format(now)
            val monthName = SimpleDateFormat("MMMM", Locale.US).format(now)
            val dayNumber = SimpleDateFormat("d", Locale.US).format(now)
            val year = SimpleDateFormat("yyyy", Locale.US).format(now)

            val bnDay = when (dayName) {
                "Friday" -> "শুক্রবার"
                "Saturday" -> "শনিবার"
                "Sunday" -> "রবিবার"
                "Monday" -> "সোমবার"
                "Tuesday" -> "মঙ্গলবার"
                "Wednesday" -> "বুধবার"
                "Thursday" -> "বৃহস্পতিবার"
                else -> dayName
            }

            val bnMonth = when (monthName) {
                "January" -> "জানুয়ারি"
                "February" -> "ফেব্রুয়ারি"
                "March" -> "মার্চ"
                "April" -> "এপ্রিল"
                "May" -> "মে"
                "June" -> "জুন"
                "July" -> "জুলাই"
                "August" -> "আগস্ট"
                "September" -> "সেপ্টেম্বর"
                "October" -> "অক্টোবর"
                "November" -> "নভেম্বর"
                "December" -> "ডিসেম্বর"
                else -> monthName
            }

            "$bnDay, ${toBengaliNumerals(dayNumber)} $bnMonth, ${toBengaliNumerals(year)}"
        } else {
            SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).format(now)
        }
    }
}
