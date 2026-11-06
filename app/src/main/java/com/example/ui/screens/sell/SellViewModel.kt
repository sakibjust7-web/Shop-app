package com.example.ui.screens.sell

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.Customer
import com.example.data.local.entity.Product
import com.example.data.local.entity.SaleOrder
import com.example.data.repository.HardwareRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CartItem(
    val product: Product,
    val quantity: Int,
    val unitPrice: Double
) {
    val lineTotal: Double get() = quantity * unitPrice
    val lineCost: Double get() = quantity * product.buyPrice
    val lineProfit: Double get() = lineTotal - lineCost
}

@OptIn(ExperimentalCoroutinesApi::class)
class SellViewModel(application: Application) : AndroidViewModel(application) {
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = _searchQuery
        .flatMapLatest { query ->
            repository.filterProducts(query, null)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _selectedProductId = MutableStateFlow<Long?>(null)
    val selectedProductId = _selectedProductId.asStateFlow()

    // Payment options: "Paid now" (default) or "Pay later" (credit)
    private val _isCreditSale = MutableStateFlow(false)
    val isCreditSale = _isCreditSale.asStateFlow()

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer = _selectedCustomer.asStateFlow()

    private val _paidNowInput = MutableStateFlow("")
    val paidNowInput = _paidNowInput.asStateFlow()

    private val _dueDateDays = MutableStateFlow<Int?>(null) // e.g. 7, 15, 30 days
    val dueDateDays = _dueDateDays.asStateFlow()

    private val _showBatchSalesSheet = MutableStateFlow(false)
    val showBatchSalesSheet = _showBatchSalesSheet.asStateFlow()

    private val _showNewCustomerDialog = MutableStateFlow(false)
    val showNewCustomerDialog = _showNewCustomerDialog.asStateFlow()

    private val _completedSaleOrder = MutableStateFlow<SaleOrder?>(null)
    val completedSaleOrder = _completedSaleOrder.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Calculated totals across all items in cart
    val totalBill: StateFlow<Double> = _cartItems.combine(MutableStateFlow(0)) { items, _ ->
        items.sumOf { it.lineTotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCost: StateFlow<Double> = _cartItems.combine(MutableStateFlow(0)) { items, _ ->
        items.sumOf { it.lineCost }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalProfit: StateFlow<Double> = _cartItems.combine(MutableStateFlow(0)) { items, _ ->
        items.sumOf { it.lineProfit }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            repository.seedInitialHardwareDataIfNeeded()
            // Auto-select first product if cart is empty to match initial screen state
            products.collect { productList ->
                if (productList.isNotEmpty() && _cartItems.value.isEmpty() && _selectedProductId.value == null) {
                    val defaultItem = productList.first()
                    selectProduct(defaultItem)
                    // If default item is Claw Hammer, set initial demo qty to 3 matching reference image
                    if (defaultItem.nameEn.contains("Hammer", ignoreCase = true) && defaultItem.stockQuantity >= 3) {
                        updateActiveQuantity(3)
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun selectProduct(product: Product) {
        _selectedProductId.value = product.id
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.product.id == product.id }
        if (existingIndex < 0) {
            val initialQty = if (product.stockQuantity > 0) 1 else 0
            currentList.add(CartItem(product = product, quantity = initialQty, unitPrice = product.sellPrice))
            _cartItems.value = currentList
        }
    }

    fun getActiveCartItem(): CartItem? {
        val currentId = _selectedProductId.value ?: return null
        return _cartItems.value.find { it.product.id == currentId }
    }

    fun updateActiveQuantity(newQty: Int) {
        val currentId = _selectedProductId.value ?: return
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == currentId }
        if (index >= 0) {
            val item = currentList[index]
            val maxStock = item.product.stockQuantity
            val clampedQty = newQty.coerceIn(1, if (maxStock > 0) maxStock else 1)
            currentList[index] = item.copy(quantity = clampedQty)
            _cartItems.value = currentList
        }
    }

    fun updateActivePrice(newPrice: Double) {
        val currentId = _selectedProductId.value ?: return
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == currentId }
        if (index >= 0) {
            val item = currentList[index]
            val validPrice = if (newPrice < 0) 0.0 else newPrice
            currentList[index] = item.copy(unitPrice = validPrice)
            _cartItems.value = currentList
        }
    }

    fun updateCartItemQuantity(productId: Long, newQty: Int) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val item = currentList[index]
            val maxStock = item.product.stockQuantity
            val clampedQty = newQty.coerceIn(1, if (maxStock > 0) maxStock else 1)
            currentList[index] = item.copy(quantity = clampedQty)
            _cartItems.value = currentList
        }
    }

    fun updateCartItemPrice(productId: Long, newPrice: Double) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val item = currentList[index]
            currentList[index] = item.copy(unitPrice = if (newPrice < 0) 0.0 else newPrice)
            _cartItems.value = currentList
        }
    }

    fun removeCartItem(productId: Long) {
        val currentList = _cartItems.value.toMutableList()
        currentList.removeAll { it.product.id == productId }
        _cartItems.value = currentList
        if (_selectedProductId.value == productId) {
            _selectedProductId.value = currentList.firstOrNull()?.product?.id
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _selectedProductId.value = null
        _isCreditSale.value = false
        _selectedCustomer.value = null
        _paidNowInput.value = ""
        _dueDateDays.value = null
    }

    fun setCreditSale(isCredit: Boolean) {
        _isCreditSale.value = isCredit
        if (!isCredit) {
            _selectedCustomer.value = null
            _paidNowInput.value = ""
            _dueDateDays.value = null
        }
    }

    fun selectCustomer(customer: Customer?) {
        _selectedCustomer.value = customer
    }

    fun setPaidNowInput(input: String) {
        _paidNowInput.value = input
    }

    fun setDueDateDays(days: Int?) {
        _dueDateDays.value = days
    }

    fun openBatchSales() {
        _showBatchSalesSheet.value = true
    }

    fun closeBatchSales() {
        _showBatchSalesSheet.value = false
    }

    fun openNewCustomerDialog() {
        _showNewCustomerDialog.value = true
    }

    fun closeNewCustomerDialog() {
        _showNewCustomerDialog.value = false
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun dismissSuccessDialog() {
        _completedSaleOrder.value = null
    }

    fun addNewCustomer(name: String, phone: String, address: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val customer = Customer(
                name = name.trim(),
                phone = phone.trim(),
                address = address.trim(),
                totalBaki = 0.0,
                lastTransactionTime = System.currentTimeMillis()
            )
            val newId = repository.insertCustomer(customer)
            _selectedCustomer.value = customer.copy(id = newId)
            closeNewCustomerDialog()
        }
    }

    fun completeSale(isBangla: Boolean) {
        val items = _cartItems.value
        if (items.isEmpty()) {
            _errorMessage.value = if (isBangla) "কার্টে কোনো পণ্য নেই!" else "No products in cart!"
            return
        }

        // Validate stock
        for (item in items) {
            if (item.quantity > item.product.stockQuantity) {
                _errorMessage.value = if (isBangla)
                    "${item.product.localizedName(true)} এর পর্যাপ্ত স্টক নেই (মওজুদ: ${item.product.stockQuantity})"
                else
                    "Insufficient stock for ${item.product.nameEn} (Available: ${item.product.stockQuantity})"
                return
            }
            if (item.quantity <= 0) {
                _errorMessage.value = if (isBangla) "পরিমাণ কমপক্ষে ১ হতে হবে" else "Quantity must be at least 1"
                return
            }
        }

        val total = totalBill.value
        val cost = totalCost.value
        val totalCount = items.sumOf { it.quantity }
        val summary = items.joinToString(", ") { "${it.product.localizedName(isBangla)} x${it.quantity}" }

        val isCredit = _isCreditSale.value
        val customer = _selectedCustomer.value

        val paidAmount: Double
        val dueAmount: Double

        if (isCredit) {
            if (customer == null) {
                _errorMessage.value = if (isBangla)
                    "বাকিতে বিক্রির জন্য গ্রাহক নির্বাচন করুন অথবা নতুন গ্রাহক যোগ করুন!"
                else
                    "Please select or add a customer for credit (Baki) sale!"
                return
            }
            val enteredPaid = _paidNowInput.value.toDoubleOrNull() ?: 0.0
            paidAmount = enteredPaid.coerceIn(0.0, total)
            dueAmount = (total - paidAmount).coerceAtLeast(0.0)
        } else {
            paidAmount = total
            dueAmount = 0.0
        }

        val now = System.currentTimeMillis()
        val dueDateTimestamp = _dueDateDays.value?.let { days ->
            now + (days.toLong() * 24 * 60 * 60 * 1000L)
        }

        val invoiceNum = "INV-${(now % 900000) + 100000}"

        val order = SaleOrder(
            invoiceNumber = invoiceNum,
            timestamp = now,
            customerId = customer?.id,
            customerName = customer?.name ?: (if (isBangla) "ক্যাশ গ্রাহক" else "Walk-in Customer"),
            customerPhone = customer?.phone ?: "",
            totalAmount = total,
            paidAmount = paidAmount,
            dueAmount = dueAmount,
            totalCost = cost,
            totalItemsCount = totalCount,
            summaryItems = summary,
            dueDate = dueDateTimestamp,
            isCredit = isCredit && dueAmount > 0
        )

        val itemsToReduce = items.map { Pair(it.product.id, it.quantity) }

        viewModelScope.launch {
            repository.completeSale(order, itemsToReduce)
            _completedSaleOrder.value = order
            // Reset for next sale
            clearCart()
        }
    }
}
