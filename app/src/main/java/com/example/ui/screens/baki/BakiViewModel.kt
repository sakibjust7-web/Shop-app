package com.example.ui.screens.baki

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.Customer
import com.example.data.local.entity.CustomerPayment
import com.example.data.local.entity.SaleOrder
import com.example.data.repository.HardwareRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class CustomerLedgerItem {
    abstract val id: Long
    abstract val timestamp: Long
    abstract val amount: Double
    abstract val note: String

    data class Sale(
        val saleOrder: SaleOrder
    ) : CustomerLedgerItem() {
        override val id: Long get() = saleOrder.id
        override val timestamp: Long get() = saleOrder.timestamp
        override val amount: Double get() = saleOrder.dueAmount
        override val note: String get() = saleOrder.summaryItems.ifBlank { "চালান #${saleOrder.invoiceNumber}" }
    }

    data class Payment(
        val customerPayment: CustomerPayment
    ) : CustomerLedgerItem() {
        override val id: Long get() = customerPayment.id
        override val timestamp: Long get() = customerPayment.timestamp
        override val amount: Double get() = customerPayment.amount
        override val note: String get() = customerPayment.note.ifBlank { "পরিশোধ / জমা" }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class BakiViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = HardwareRepository(
        database.productDao(),
        database.saleOrderDao(),
        database.categoryDao(),
        database.purchaseDao(),
        database.customerDao(),
        database.supplierDao(),
        database.supplierPaymentDao(),
        database.customerPaymentDao()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer = _selectedCustomer.asStateFlow()

    private val _showAddCustomerDialog = MutableStateFlow(false)
    val showAddCustomerDialog = _showAddCustomerDialog.asStateFlow()

    private val _showRecordPaymentDialog = MutableStateFlow(false)
    val showRecordPaymentDialog = _showRecordPaymentDialog.asStateFlow()

    private val _showAddNoteDialog = MutableStateFlow(false)
    val showAddNoteDialog = _showAddNoteDialog.asStateFlow()

    private val _entryToEdit = MutableStateFlow<CustomerLedgerItem?>(null)
    val entryToEdit = _entryToEdit.asStateFlow()

    private val _entryToDelete = MutableStateFlow<CustomerLedgerItem?>(null)
    val entryToDelete = _entryToDelete.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialHardwareDataIfNeeded()
        }
    }

    // Customers with biggest balance first (ORDER BY totalBaki DESC, name ASC)
    val allCustomers: StateFlow<List<Customer>> = repository.allCustomersBakiFirst
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCustomers: StateFlow<List<Customer>> = combine(
        repository.allCustomersBakiFirst,
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            val q = query.trim().lowercase()
            list.filter {
                it.name.lowercase().contains(q) ||
                    it.phone.contains(q) ||
                    it.address.lowercase().contains(q) ||
                    it.note.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCustomerBaki: StateFlow<Double> = repository.allCustomersBakiFirst
        .combine(MutableStateFlow(0.0)) { list, _ -> list.sumOf { it.totalBaki } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Credit sales for selected customer
    val customerCreditSales: StateFlow<List<SaleOrder>> = _selectedCustomer
        .flatMapLatest { customer ->
            if (customer != null) {
                repository.getCreditSalesForCustomer(customer.id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Payments for selected customer
    val customerPayments: StateFlow<List<CustomerPayment>> = _selectedCustomer
        .flatMapLatest { customer ->
            if (customer != null) {
                repository.getPaymentsForCustomer(customer.id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined ledger entries (credit sales + payments) sorted descending by timestamp
    val customerLedger: StateFlow<List<CustomerLedgerItem>> = combine(
        customerCreditSales,
        customerPayments
    ) { sales, payments ->
        val saleItems = sales.map { CustomerLedgerItem.Sale(it) }
        val paymentItems = payments.map { CustomerLedgerItem.Payment(it) }
        (saleItems + paymentItems).sortedByDescending { it.timestamp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun selectCustomer(customer: Customer?) {
        _selectedCustomer.value = customer
    }

    fun refreshSelectedCustomer() {
        val current = _selectedCustomer.value ?: return
        viewModelScope.launch {
            val updated = database.customerDao().getCustomerByIdSync(current.id)
            _selectedCustomer.value = updated
        }
    }

    fun openAddCustomerDialog() {
        _showAddCustomerDialog.value = true
    }

    fun closeAddCustomerDialog() {
        _showAddCustomerDialog.value = false
    }

    fun openRecordPaymentDialog() {
        _showRecordPaymentDialog.value = true
    }

    fun closeRecordPaymentDialog() {
        _showRecordPaymentDialog.value = false
    }

    fun openAddNoteDialog() {
        _showAddNoteDialog.value = true
    }

    fun closeAddNoteDialog() {
        _showAddNoteDialog.value = false
    }

    fun openEditEntry(entry: CustomerLedgerItem) {
        _entryToEdit.value = entry
    }

    fun closeEditEntry() {
        _entryToEdit.value = null
    }

    fun openDeleteEntry(entry: CustomerLedgerItem) {
        _entryToDelete.value = entry
    }

    fun closeDeleteEntry() {
        _entryToDelete.value = null
    }

    fun addCustomer(
        name: String,
        phone: String,
        address: String,
        note: String,
        initialBaki: Double = 0.0,
        dueDate: Long? = null
    ) {
        viewModelScope.launch {
            val customer = Customer(
                name = name.trim(),
                phone = phone.trim(),
                address = address.trim(),
                note = note.trim(),
                totalBaki = initialBaki,
                dueDate = dueDate,
                lastTransactionTime = System.currentTimeMillis()
            )
            repository.insertCustomer(customer)
            closeAddCustomerDialog()
        }
    }

    fun recordPayment(amount: Double, note: String) {
        val customer = _selectedCustomer.value ?: return
        if (amount <= 0) return
        viewModelScope.launch {
            repository.recordCustomerPayment(
                customerId = customer.id,
                amount = amount,
                note = note
            )
            refreshSelectedCustomer()
            closeRecordPaymentDialog()
        }
    }

    fun addCustomerNote(newNote: String) {
        val customer = _selectedCustomer.value ?: return
        viewModelScope.launch {
            val existing = customer.note.trim()
            val combined = if (existing.isBlank()) newNote.trim() else "$existing\n$newNote"
            repository.updateCustomerNote(customer.id, combined)
            refreshSelectedCustomer()
            closeAddNoteDialog()
        }
    }

    fun updatePaymentEntry(paymentId: Long, newAmount: Double, newNote: String) {
        val customer = _selectedCustomer.value ?: return
        viewModelScope.launch {
            val payment = CustomerPayment(
                id = paymentId,
                customerId = customer.id,
                amount = newAmount,
                note = newNote
            )
            repository.updateCustomerPayment(payment)
            refreshSelectedCustomer()
            closeEditEntry()
        }
    }

    fun deletePaymentEntry(paymentId: Long) {
        val customer = _selectedCustomer.value ?: return
        viewModelScope.launch {
            repository.deleteCustomerPayment(paymentId, customer.id)
            refreshSelectedCustomer()
            closeDeleteEntry()
        }
    }

    fun updateCreditSaleEntry(saleOrder: SaleOrder, newDueAmount: Double, newNote: String, newDueDate: Long?) {
        val customer = _selectedCustomer.value ?: return
        viewModelScope.launch {
            val updated = saleOrder.copy(
                dueAmount = newDueAmount,
                summaryItems = newNote,
                dueDate = newDueDate
            )
            repository.updateCreditSale(updated)
            refreshSelectedCustomer()
            closeEditEntry()
        }
    }

    fun deleteCreditSaleEntry(saleId: Long) {
        val customer = _selectedCustomer.value ?: return
        viewModelScope.launch {
            repository.deleteCreditSale(saleId, customer.id)
            refreshSelectedCustomer()
            closeDeleteEntry()
        }
    }
}
