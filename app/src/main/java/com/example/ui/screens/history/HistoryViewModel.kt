package com.example.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.Product
import com.example.data.local.entity.PurchaseRecord
import com.example.data.local.entity.SaleOrder
import com.example.data.repository.HardwareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class TimePeriodFilter {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    ALL,
    CUSTOM
}

enum class TransactionTypeFilter {
    ALL,
    SALES,
    PURCHASES
}

data class HistoryItem(
    val id: String,
    val type: TransactionTypeFilter, // SALES or PURCHASES
    val timestamp: Long,
    val title: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalAmount: Double,
    val profit: Double,
    val isCredit: Boolean = false,
    val partyName: String = "",
    val partyPhone: String = "",
    val referenceNumber: String = "",
    val photoKey: String = "",
    val category: String = ""
)

data class DayGroupedHistory(
    val dateKey: String,
    val dateLabelEn: String,
    val dateLabelBn: String,
    val timestamp: Long,
    val items: List<HistoryItem>
)

data class HistoryPeriodSummary(
    val bought: Double = 0.0,
    val sold: Double = 0.0,
    val profit: Double = 0.0
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
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

    private val _timeFilter = MutableStateFlow(TimePeriodFilter.TODAY)
    val timeFilter = _timeFilter.asStateFlow()

    private val _typeFilter = MutableStateFlow(TransactionTypeFilter.ALL)
    val typeFilter = _typeFilter.asStateFlow()

    private val _isDetailedView = MutableStateFlow(true)
    val isDetailedView = _isDetailedView.asStateFlow()

    private val _customStartDate = MutableStateFlow<Long?>(null)
    val customStartDate = _customStartDate.asStateFlow()

    private val _customEndDate = MutableStateFlow<Long?>(null)
    val customEndDate = _customEndDate.asStateFlow()

    private val _showDatePickerDialog = MutableStateFlow(false)
    val showDatePickerDialog = _showDatePickerDialog.asStateFlow()

    private val _selectedItemForDetail = MutableStateFlow<HistoryItem?>(null)
    val selectedItemForDetail = _selectedItemForDetail.asStateFlow()

    private val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allSales: StateFlow<List<SaleOrder>> = repository.allSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allPurchases: StateFlow<List<PurchaseRecord>> = repository.allPurchases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined Raw History Items
    private val combinedItems = combine(allSales, allPurchases, allProducts) { sales, purchases, products ->
        val list = mutableListOf<HistoryItem>()

        // 1. Map Sales
        for (sale in sales) {
            val photoKey = inferPhotoKey(sale.summaryItems, products)
            val unitPrice = if (sale.totalItemsCount > 0) sale.totalAmount / sale.totalItemsCount else sale.totalAmount
            list.add(
                HistoryItem(
                    id = "sale_${sale.id}",
                    type = TransactionTypeFilter.SALES,
                    timestamp = sale.timestamp,
                    title = sale.summaryItems.ifBlank { "Sale #${sale.invoiceNumber}" },
                    quantity = sale.totalItemsCount,
                    unitPrice = unitPrice,
                    totalAmount = sale.totalAmount,
                    profit = sale.profit,
                    isCredit = sale.isCredit,
                    partyName = sale.customerName,
                    partyPhone = sale.customerPhone,
                    referenceNumber = sale.invoiceNumber,
                    photoKey = photoKey,
                    category = "Sale"
                )
            )
        }

        // 2. Map Purchases
        for (purchase in purchases) {
            val matchedProduct = products.find { it.id == purchase.productId }
            val photoKey = matchedProduct?.photoKey ?: inferPhotoKey(purchase.productName, products)
            val category = matchedProduct?.category ?: "Purchase"
            list.add(
                HistoryItem(
                    id = "purch_${purchase.id}",
                    type = TransactionTypeFilter.PURCHASES,
                    timestamp = purchase.timestamp,
                    title = purchase.productName,
                    quantity = purchase.quantity,
                    unitPrice = purchase.unitBuyingPrice,
                    totalAmount = purchase.totalCost,
                    profit = 0.0,
                    isCredit = false,
                    partyName = purchase.supplierName,
                    partyPhone = "",
                    referenceNumber = "PO-${purchase.id}",
                    photoKey = photoKey,
                    category = category
                )
            )
        }

        // Newest first
        list.sortedByDescending { it.timestamp }
    }

    // Filtered Items based on Time Filter and Type Filter
    val filteredGroupedHistory: StateFlow<List<DayGroupedHistory>> = combine(
        combinedItems,
        _timeFilter,
        _typeFilter,
        _customStartDate,
        _customEndDate
    ) { items, timePeriod, typeFilter, customStart, customEnd ->
        val (startTime, endTime) = getTimeRangeBounds(timePeriod, customStart, customEnd)

        val filtered = items.filter { item ->
            val withinTime = item.timestamp in startTime..endTime
            val matchesType = when (typeFilter) {
                TransactionTypeFilter.ALL -> true
                TransactionTypeFilter.SALES -> item.type == TransactionTypeFilter.SALES
                TransactionTypeFilter.PURCHASES -> item.type == TransactionTypeFilter.PURCHASES
            }
            withinTime && matchesType
        }

        // Group by Day
        groupByDay(filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Summary Card Totals for the Selected Time Period
    val periodSummary: StateFlow<HistoryPeriodSummary> = combine(
        combinedItems,
        _timeFilter,
        _customStartDate,
        _customEndDate
    ) { items, timePeriod, customStart, customEnd ->
        val (startTime, endTime) = getTimeRangeBounds(timePeriod, customStart, customEnd)

        var bought = 0.0
        var sold = 0.0
        var profit = 0.0

        for (item in items) {
            if (item.timestamp in startTime..endTime) {
                if (item.type == TransactionTypeFilter.PURCHASES) {
                    bought += item.totalAmount
                } else if (item.type == TransactionTypeFilter.SALES) {
                    sold += item.totalAmount
                    profit += item.profit
                }
            }
        }

        HistoryPeriodSummary(bought = bought, sold = sold, profit = profit)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryPeriodSummary())

    init {
        viewModelScope.launch {
            repository.seedInitialHardwareDataIfNeeded()
        }
    }

    fun setTimeFilter(filter: TimePeriodFilter) {
        _timeFilter.value = filter
        if (filter == TimePeriodFilter.CUSTOM) {
            _showDatePickerDialog.value = true
        }
    }

    fun setTypeFilter(filter: TransactionTypeFilter) {
        _typeFilter.value = filter
    }

    fun toggleDetailedView() {
        _isDetailedView.value = !_isDetailedView.value
    }

    fun setCustomDateRange(startMs: Long, endMs: Long) {
        _customStartDate.value = startMs
        _customEndDate.value = endMs
        _timeFilter.value = TimePeriodFilter.CUSTOM
        _showDatePickerDialog.value = false
    }

    fun openDatePickerDialog() {
        _showDatePickerDialog.value = true
    }

    fun closeDatePickerDialog() {
        _showDatePickerDialog.value = false
    }

    fun selectItemForDetail(item: HistoryItem?) {
        _selectedItemForDetail.value = item
    }

    private fun getTimeRangeBounds(
        period: TimePeriodFilter,
        customStart: Long?,
        customEnd: Long?
    ): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        return when (period) {
            TimePeriodFilter.TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimePeriodFilter.THIS_WEEK -> {
                cal.add(Calendar.DAY_OF_YEAR, -7)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                Pair(start, now + 86400000L)
            }
            TimePeriodFilter.THIS_MONTH -> {
                cal.add(Calendar.DAY_OF_YEAR, -30)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                Pair(start, now + 86400000L)
            }
            TimePeriodFilter.ALL -> {
                Pair(0L, Long.MAX_VALUE)
            }
            TimePeriodFilter.CUSTOM -> {
                val start = customStart ?: (now - 7 * 86400000L)
                val end = customEnd ?: now
                Pair(start, end)
            }
        }
    }

    private fun groupByDay(items: List<HistoryItem>): List<DayGroupedHistory> {
        val groups = LinkedHashMap<String, MutableList<HistoryItem>>()
        val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val displayFormat = SimpleDateFormat("d MMM", Locale.ENGLISH)

        val calToday = Calendar.getInstance()
        val todayStr = dayFormat.format(calToday.time)

        calToday.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dayFormat.format(calToday.time)

        for (item in items) {
            val key = dayFormat.format(item.timestamp)
            groups.getOrPut(key) { mutableListOf() }.add(item)
        }

        return groups.map { (key, dayItems) ->
            val firstTimestamp = dayItems.first().timestamp
            val (labelEn, labelBn) = when (key) {
                todayStr -> Pair("Today", "আজ")
                yesterdayStr -> Pair("Yesterday", "গতকাল")
                else -> {
                    val formatted = displayFormat.format(firstTimestamp)
                    Pair(formatted, translateDateToBn(formatted))
                }
            }
            DayGroupedHistory(
                dateKey = key,
                dateLabelEn = labelEn,
                dateLabelBn = labelBn,
                timestamp = firstTimestamp,
                items = dayItems
            )
        }
    }

    private fun translateDateToBn(dateStr: String): String {
        // e.g. "5 Nov" -> "৫ নভেম্বর"
        val parts = dateStr.split(" ")
        if (parts.size >= 2) {
            val day = com.example.ui.localization.toBengaliNumerals(parts[0])
            val month = when (parts[1].lowercase()) {
                "jan" -> "জানুয়ারি"
                "feb" -> "ফেব্রুয়ারি"
                "mar" -> "মার্চ"
                "apr" -> "এপ্রিল"
                "may" -> "মে"
                "jun" -> "জুন"
                "jul" -> "জুলাই"
                "aug" -> "আগস্ট"
                "sep" -> "সেপ্টেম্বর"
                "oct" -> "অক্টোবর"
                "nov" -> "নভেম্বর"
                "dec" -> "ডিসেম্বর"
                else -> parts[1]
            }
            return "$day $month"
        }
        return dateStr
    }

    private fun inferPhotoKey(text: String, products: List<Product>): String {
        val lower = text.lowercase()
        return when {
            lower.contains("hammer") || lower.contains("হাতুড়ি") -> "hammer"
            lower.contains("pipe") || lower.contains("পাইপ") -> "pvc_pipe"
            lower.contains("paint") || lower.contains("রং") -> "paint"
            lower.contains("lock") || lower.contains("তালা") -> "lock"
            lower.contains("switch") || lower.contains("সুইচ") -> "switch"
            lower.contains("drill") || lower.contains("ড্রিল") -> "drill"
            lower.contains("nail") || lower.contains("পেরেক") -> "nails"
            lower.contains("wire") || lower.contains("তার") -> "wire"
            lower.contains("light") || lower.contains("বাল্ব") -> "light"
            else -> {
                val matched = products.find { lower.contains(it.nameEn.lowercase()) || lower.contains(it.nameBn.lowercase()) }
                matched?.photoKey ?: "hammer"
            }
        }
    }
}
