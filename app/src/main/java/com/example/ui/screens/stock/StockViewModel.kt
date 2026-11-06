package com.example.ui.screens.stock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.Category
import com.example.data.local.entity.Product
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

@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModel(application: Application) : AndroidViewModel(application) {
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

    private val _selectedCategory = MutableStateFlow<String?>(null) // null = All
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _isGridView = MutableStateFlow(true) // Default to 3-column grid matching reference image
    val isGridView = _isGridView.asStateFlow()

    val categories: StateFlow<List<Category>> = repository.visibleCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suppliers: StateFlow<List<com.example.data.local.entity.Supplier>> = repository.allSuppliersOwedFirst
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = combine(_searchQuery, _selectedCategory) { query, cat ->
        Pair(query, cat)
    }.flatMapLatest { (query, cat) ->
        repository.filterProducts(query, if (cat == "All") null else cat)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalProductCount: StateFlow<Int> = repository.allProducts
        .combine(MutableStateFlow(0)) { list, _ -> list.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // UI Dialog & Sheet States
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct = _selectedProduct.asStateFlow()

    private val _showAddProductDialog = MutableStateFlow(false)
    val showAddProductDialog = _showAddProductDialog.asStateFlow()

    private val _productToEdit = MutableStateFlow<Product?>(null)
    val productToEdit = _productToEdit.asStateFlow()

    private val _productForAddStock = MutableStateFlow<Product?>(null)
    val productForAddStock = _productForAddStock.asStateFlow()

    private val _showCategoryManager = MutableStateFlow(false)
    val showCategoryManager = _showCategoryManager.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialHardwareDataIfNeeded()
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(categoryNameEn: String?) {
        _selectedCategory.value = categoryNameEn
    }

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    fun selectProduct(product: Product?) {
        _selectedProduct.value = product
    }

    fun openAddProductDialog() {
        _showAddProductDialog.value = true
    }

    fun closeAddProductDialog() {
        _showAddProductDialog.value = false
    }

    fun openEditProduct(product: Product) {
        _productToEdit.value = product
    }

    fun closeEditProduct() {
        _productToEdit.value = null
    }

    fun openAddStock(product: Product) {
        _productForAddStock.value = product
    }

    fun closeAddStock() {
        _productForAddStock.value = null
    }

    fun openCategoryManager() {
        _showCategoryManager.value = true
    }

    fun closeCategoryManager() {
        _showCategoryManager.value = false
    }

    fun saveNewProduct(
        nameBn: String,
        nameEn: String,
        category: String,
        description: String,
        photoKey: String,
        stockQuantity: Int,
        buyPrice: Double,
        sellPrice: Double,
        lowStockThreshold: Int = 5,
        unitBn: String = "টি",
        unitEn: String = "pcs"
    ) {
        viewModelScope.launch {
            val product = Product(
                nameBn = nameBn.ifBlank { nameEn },
                nameEn = nameEn.ifBlank { nameBn },
                category = category.ifBlank { "Other" },
                description = description,
                photoKey = photoKey,
                stockQuantity = stockQuantity,
                buyPrice = buyPrice,
                sellPrice = sellPrice,
                lowStockThreshold = lowStockThreshold,
                unitBn = unitBn,
                unitEn = unitEn
            )
            repository.insertProduct(product)
            closeAddProductDialog()
        }
    }

    fun updateExistingProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
            _selectedProduct.value = product
            closeEditProduct()
        }
    }

    fun softDeleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.softDeleteProduct(productId)
            if (_selectedProduct.value?.id == productId) {
                _selectedProduct.value = null
            }
        }
    }

    fun recordAddStock(
        productId: Long,
        productName: String,
        addedQty: Int,
        unitBuyingPrice: Double,
        supplierId: Long? = null,
        supplierName: String = "",
        paidAmount: Double = 0.0,
        dueAmount: Double = 0.0,
        billPhotoUri: String? = null,
        note: String = ""
    ) {
        viewModelScope.launch {
            repository.addStock(
                productId = productId,
                productName = productName,
                addedQty = addedQty,
                unitBuyingPrice = unitBuyingPrice,
                supplierName = supplierName,
                note = note,
                supplierId = supplierId,
                paidAmount = paidAmount,
                dueAmount = dueAmount,
                billPhotoUri = billPhotoUri
            )
            closeAddStock()
            // Refresh currently selected product view if open
            _selectedProduct.value = _selectedProduct.value?.let {
                it.copy(
                    stockQuantity = it.stockQuantity + addedQty,
                    buyPrice = if (unitBuyingPrice > 0) unitBuyingPrice else it.buyPrice
                )
            }
        }
    }

    fun addSupplierQuick(name: String, company: String, phone: String, onCreated: (com.example.data.local.entity.Supplier) -> Unit) {
        viewModelScope.launch {
            val supplier = com.example.data.local.entity.Supplier(
                name = name,
                company = company,
                phone = phone
            )
            val id = repository.insertSupplier(supplier)
            onCreated(supplier.copy(id = id))
        }
    }

    fun addCategory(nameEn: String, nameBn: String, emoji: String) {
        viewModelScope.launch {
            val count = allCategories.value.size
            repository.insertCategory(
                Category(
                    nameEn = nameEn,
                    nameBn = nameBn.ifBlank { nameEn },
                    emoji = emoji.ifBlank { "📦" },
                    sortOrder = count + 1,
                    isVisible = true
                )
            )
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun toggleCategoryVisibility(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category.copy(isVisible = !category.isVisible))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}
