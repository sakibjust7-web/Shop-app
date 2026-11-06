package com.example.data.repository

import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.CustomerDao
import com.example.data.local.dao.CustomerPaymentDao
import com.example.data.local.dao.ExpenseDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.PurchaseDao
import com.example.data.local.dao.SaleOrderDao
import com.example.data.local.dao.SupplierDao
import com.example.data.local.dao.SupplierPaymentDao
import com.example.data.local.entity.Category
import com.example.data.local.entity.Customer
import com.example.data.local.entity.CustomerPayment
import com.example.data.local.entity.Expense
import com.example.data.local.entity.Product
import com.example.data.local.entity.PurchaseRecord
import com.example.data.local.entity.SaleOrder
import com.example.data.local.entity.Supplier
import com.example.data.local.entity.SupplierPayment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import java.util.Calendar

class HardwareRepository(
    private val productDao: ProductDao,
    private val saleOrderDao: SaleOrderDao,
    private val categoryDao: CategoryDao,
    private val purchaseDao: PurchaseDao,
    private val customerDao: CustomerDao,
    private val supplierDao: SupplierDao? = null,
    private val supplierPaymentDao: SupplierPaymentDao? = null,
    private val customerPaymentDao: CustomerPaymentDao? = null,
    private val expenseDao: ExpenseDao? = null
) {
    val lowStockProducts: Flow<List<Product>> = productDao.getLowStockProducts(limit = 5)
    val recentSales: Flow<List<SaleOrder>> = saleOrderDao.getRecentSales(limit = 5)
    val totalUnpaidBaki: Flow<Double?> = saleOrderDao.getTotalUnpaidBaki()
    val visibleCategories: Flow<List<Category>> = categoryDao.getVisibleCategories()
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    val allSales: Flow<List<SaleOrder>> = saleOrderDao.getAllSales()
    val allPurchases: Flow<List<PurchaseRecord>> = purchaseDao.getAllPurchases()
    val allExpenses: Flow<List<Expense>> = expenseDao?.getAllExpenses() ?: flowOf(emptyList())
    val allCustomerPayments: Flow<List<CustomerPayment>> = customerPaymentDao?.getAllPayments() ?: flowOf(emptyList())
    val allSupplierPayments: Flow<List<SupplierPayment>> = supplierPaymentDao?.getAllPayments() ?: flowOf(emptyList())

    val allSuppliersOwedFirst: Flow<List<Supplier>> =
        supplierDao?.getAllSuppliersOwedFirst() ?: flowOf(emptyList())

    val totalAmountOwedToSuppliers: Flow<Double?> =
        supplierDao?.getTotalAmountOwed() ?: flowOf(0.0)

    fun getSupplierById(id: Long): Flow<Supplier?> =
        supplierDao?.getSupplierById(id) ?: flowOf(null)

    fun getSupplierPurchases(supplierId: Long, supplierName: String = ""): Flow<List<PurchaseRecord>> =
        purchaseDao.getPurchasesForSupplierOrName(supplierId, supplierName)

    fun getSupplierPayments(supplierId: Long): Flow<List<SupplierPayment>> =
        supplierPaymentDao?.getPaymentsForSupplier(supplierId) ?: flowOf(emptyList())

    suspend fun insertSupplier(supplier: Supplier): Long = withContext(Dispatchers.IO) {
        supplierDao?.insertSupplier(supplier) ?: 0L
    }

    suspend fun updateSupplier(supplier: Supplier) = withContext(Dispatchers.IO) {
        supplierDao?.updateSupplier(supplier)
    }

    suspend fun deleteSupplier(supplier: Supplier) = withContext(Dispatchers.IO) {
        supplierDao?.deleteSupplier(supplier)
    }

    suspend fun recordSupplierPayment(
        supplierId: Long,
        amount: Double,
        paymentMethod: String = "Cash",
        note: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val payment = SupplierPayment(
            supplierId = supplierId,
            amount = amount,
            paymentMethod = paymentMethod,
            note = note,
            timestamp = System.currentTimeMillis()
        )
        val paymentId = supplierPaymentDao?.insertPayment(payment) ?: 0L
        supplierDao?.adjustAmountOwed(supplierId, -amount)
        paymentId
    }

    fun filterProducts(query: String, category: String?): Flow<List<Product>> {
        return productDao.filterProducts(query.trim(), category)
    }

    suspend fun insertProduct(product: Product): Long = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun softDeleteProduct(productId: Long) = withContext(Dispatchers.IO) {
        productDao.softDeleteProduct(productId)
    }

    suspend fun addStock(
        productId: Long,
        productName: String,
        addedQty: Int,
        unitBuyingPrice: Double,
        supplierName: String = "",
        note: String = "",
        supplierId: Long? = null,
        paidAmount: Double = 0.0,
        dueAmount: Double = 0.0,
        billPhotoUri: String? = null
    ) = withContext(Dispatchers.IO) {
        productDao.addStock(productId, addedQty, unitBuyingPrice)
        purchaseDao.insertPurchase(
            PurchaseRecord(
                productId = productId,
                productName = productName,
                quantity = addedQty,
                unitBuyingPrice = unitBuyingPrice,
                totalCost = addedQty * unitBuyingPrice,
                supplierName = supplierName,
                note = note,
                supplierId = supplierId,
                paidAmount = paidAmount,
                dueAmount = dueAmount,
                billPhotoUri = billPhotoUri
            )
        )
        if (supplierId != null && dueAmount > 0) {
            supplierDao?.adjustAmountOwed(supplierId, dueAmount)
        }
    }

    val allCustomersBakiFirst: Flow<List<Customer>> = customerDao.getAllCustomers()

    fun getCustomerById(id: Long): Flow<Customer?> = customerDao.getCustomerById(id)

    fun getCreditSalesForCustomer(customerId: Long): Flow<List<SaleOrder>> =
        saleOrderDao.getCreditSalesForCustomer(customerId)

    fun getPaymentsForCustomer(customerId: Long): Flow<List<CustomerPayment>> =
        customerPaymentDao?.getPaymentsForCustomer(customerId) ?: flowOf(emptyList())

    suspend fun insertCustomer(customer: Customer): Long = withContext(Dispatchers.IO) {
        val id = customerDao.insertCustomer(customer)
        if (customer.totalBaki > 0) {
            saleOrderDao.insertSale(
                SaleOrder(
                    invoiceNumber = "OPN-${System.currentTimeMillis() % 10000}",
                    customerId = id,
                    customerName = customer.name,
                    customerPhone = customer.phone,
                    totalAmount = customer.totalBaki,
                    paidAmount = 0.0,
                    dueAmount = customer.totalBaki,
                    totalCost = customer.totalBaki * 0.75,
                    totalItemsCount = 1,
                    summaryItems = "পূর্ববর্তী বকেয়া (Opening Balance)",
                    dueDate = customer.dueDate,
                    isCredit = true
                )
            )
        }
        id
    }

    suspend fun updateCustomer(customer: Customer) = withContext(Dispatchers.IO) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) = withContext(Dispatchers.IO) {
        customerDao.deleteCustomer(customer)
    }

    suspend fun updateCustomerNote(customerId: Long, note: String) = withContext(Dispatchers.IO) {
        customerDao.updateCustomerNote(customerId, note)
    }

    suspend fun updateCustomerDueDate(customerId: Long, dueDate: Long?) = withContext(Dispatchers.IO) {
        customerDao.updateCustomerDueDate(customerId, dueDate)
    }

    suspend fun recordCustomerPayment(
        customerId: Long,
        amount: Double,
        note: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val payment = CustomerPayment(
            customerId = customerId,
            amount = amount,
            note = note,
            timestamp = System.currentTimeMillis()
        )
        val id = customerPaymentDao?.insertPayment(payment) ?: 0L
        recalculateCustomerBalance(customerId)
        id
    }

    suspend fun updateCustomerPayment(
        payment: CustomerPayment
    ) = withContext(Dispatchers.IO) {
        customerPaymentDao?.updatePayment(payment)
        recalculateCustomerBalance(payment.customerId)
    }

    suspend fun deleteCustomerPayment(
        paymentId: Long,
        customerId: Long
    ) = withContext(Dispatchers.IO) {
        customerPaymentDao?.deletePaymentById(paymentId)
        recalculateCustomerBalance(customerId)
    }

    suspend fun updateCreditSale(
        saleOrder: SaleOrder
    ) = withContext(Dispatchers.IO) {
        saleOrderDao.updateSale(saleOrder)
        saleOrder.customerId?.let { recalculateCustomerBalance(it) }
    }

    suspend fun deleteCreditSale(
        saleId: Long,
        customerId: Long
    ) = withContext(Dispatchers.IO) {
        saleOrderDao.deleteSaleById(saleId)
        recalculateCustomerBalance(customerId)
    }

    suspend fun recalculateCustomerBalance(customerId: Long) = withContext(Dispatchers.IO) {
        val creditSales = saleOrderDao.getCreditSalesForCustomerSync(customerId)
        val payments = customerPaymentDao?.getPaymentsForCustomerSync(customerId) ?: emptyList()
        val totalCreditDue = creditSales.sumOf { it.dueAmount }
        val totalPaid = payments.sumOf { it.amount }
        val newBalance = (totalCreditDue - totalPaid).coerceAtLeast(0.0)
        customerDao.updateCustomerBalance(customerId, newBalance)
    }

    suspend fun updateProductStockQuantity(productId: Long, newStock: Int) = withContext(Dispatchers.IO) {
        productDao.updateStockQuantity(productId, newStock)
    }

    suspend fun updateSupplierBalance(supplierId: Long, newBalance: Double) = withContext(Dispatchers.IO) {
        supplierDao?.updateSupplierBalance(supplierId, newBalance)
    }

    suspend fun updateCustomerBalance(customerId: Long, newBalance: Double) = withContext(Dispatchers.IO) {
        customerDao.updateCustomerBalance(customerId, newBalance)
    }

    /**
     * Completes a sale transaction:
     * 1. Inserts the sale order record
     * 2. Reduces stock quantity for every item sold
     * 3. If credit sale, adds the unpaid due amount to the customer's balance
     */
    suspend fun completeSale(
        order: SaleOrder,
        itemsToReduce: List<Pair<Long, Int>>
    ): Long = withContext(Dispatchers.IO) {
        // 1. Insert order
        val orderId = saleOrderDao.insertSale(order)

        // 2. Reduce stock for each product
        for ((productId, qty) in itemsToReduce) {
            productDao.reduceStock(productId, qty)
        }

        // 3. Update customer baki if credit
        if (order.dueAmount > 0) {
            if (order.customerId != null && order.customerId > 0) {
                customerDao.addBaki(order.customerId, order.dueAmount, order.timestamp)
            } else if (order.customerName.isNotBlank()) {
                val newCustId = customerDao.insertCustomer(
                    Customer(
                        name = order.customerName,
                        phone = order.customerPhone,
                        totalBaki = order.dueAmount,
                        lastTransactionTime = order.timestamp
                    )
                )
            }
        }

        orderId
    }

    suspend fun insertCategory(category: Category): Long = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) = withContext(Dispatchers.IO) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategory(category)
    }

    suspend fun getAllCategoriesSync(): List<Category> = withContext(Dispatchers.IO) {
        categoryDao.getAllCategoriesSync()
    }

    suspend fun moveCategory(category: Category, moveUp: Boolean) = withContext(Dispatchers.IO) {
        val all = categoryDao.getAllCategoriesSync().toMutableList()
        val index = all.indexOfFirst { it.id == category.id }
        if (index == -1) return@withContext
        val targetIndex = if (moveUp) index - 1 else index + 1
        if (targetIndex in all.indices) {
            val current = all[index]
            val neighbor = all[targetIndex]
            val currentOrder = current.sortOrder
            val neighborOrder = neighbor.sortOrder
            val newCurrentOrder = if (currentOrder == neighborOrder) (if (moveUp) neighborOrder - 1 else neighborOrder + 1) else neighborOrder
            val newNeighborOrder = currentOrder
            categoryDao.updateCategory(current.copy(sortOrder = newCurrentOrder))
            categoryDao.updateCategory(neighbor.copy(sortOrder = newNeighborOrder))
        }
    }

    suspend fun setCategoryVisibility(category: Category, isVisible: Boolean) = withContext(Dispatchers.IO) {
        categoryDao.updateVisibility(category.id, isVisible)
    }

    // Sync methods for full backup
    suspend fun getAllProductsSync(): List<Product> = withContext(Dispatchers.IO) {
        productDao.getAllProductsSync()
    }

    suspend fun getAllSalesSync(): List<SaleOrder> = withContext(Dispatchers.IO) {
        saleOrderDao.getAllSalesSync()
    }

    suspend fun getAllCustomersSync(): List<Customer> = withContext(Dispatchers.IO) {
        customerDao.getAllCustomersSync()
    }

    suspend fun getAllCustomerPaymentsSync(): List<CustomerPayment> = withContext(Dispatchers.IO) {
        customerPaymentDao?.getAllPaymentsSync() ?: emptyList()
    }

    suspend fun getAllSuppliersSync(): List<Supplier> = withContext(Dispatchers.IO) {
        supplierDao?.getAllSuppliersSync() ?: emptyList()
    }

    suspend fun getAllSupplierPaymentsSync(): List<SupplierPayment> = withContext(Dispatchers.IO) {
        supplierPaymentDao?.getAllPaymentsSync() ?: emptyList()
    }

    suspend fun getAllPurchasesSync(): List<PurchaseRecord> = withContext(Dispatchers.IO) {
        purchaseDao.getAllPurchasesSync()
    }

    suspend fun getAllExpensesSync(): List<Expense> = withContext(Dispatchers.IO) {
        expenseDao?.getAllExpensesSync() ?: emptyList()
    }

    // Clear and restore methods
    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        productDao.clearAll()
        saleOrderDao.clearAll()
        customerDao.clearAll()
        customerPaymentDao?.clearAll()
        supplierDao?.clearAll()
        supplierPaymentDao?.clearAll()
        purchaseDao.clearAll()
        expenseDao?.clearAll()
        categoryDao.clearAll()
    }

    suspend fun insertCategories(categories: List<Category>) = withContext(Dispatchers.IO) {
        categoryDao.insertAll(categories)
    }

    suspend fun insertProducts(products: List<Product>) = withContext(Dispatchers.IO) {
        productDao.insertAll(products)
    }

    suspend fun insertCustomers(customers: List<Customer>) = withContext(Dispatchers.IO) {
        customerDao.insertAll(customers)
    }

    suspend fun insertCustomerPayments(payments: List<CustomerPayment>) = withContext(Dispatchers.IO) {
        customerPaymentDao?.insertAll(payments)
    }

    suspend fun insertSuppliers(suppliers: List<Supplier>) = withContext(Dispatchers.IO) {
        supplierDao?.insertAll(suppliers)
    }

    suspend fun insertSupplierPayments(payments: List<SupplierPayment>) = withContext(Dispatchers.IO) {
        supplierPaymentDao?.insertAll(payments)
    }

    suspend fun insertPurchases(purchases: List<PurchaseRecord>) = withContext(Dispatchers.IO) {
        purchaseDao.insertAll(purchases)
    }

    suspend fun insertSales(sales: List<SaleOrder>) = withContext(Dispatchers.IO) {
        saleOrderDao.insertAll(sales)
    }

    suspend fun insertExpenses(expenses: List<Expense>) = withContext(Dispatchers.IO) {
        expenseDao?.insertAll(expenses)
    }

    fun getTodaySales(): Flow<List<SaleOrder>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis

        return saleOrderDao.getSalesBetween(startTime, endTime)
    }

    suspend fun seedInitialHardwareDataIfNeeded() = withContext(Dispatchers.IO) {
        // 1. Seed Categories if empty
        if (categoryDao.getCategoryCount() == 0) {
            val defaultCategories = listOf(
                Category(nameEn = "Hand Tools", nameBn = "হ্যান্ড টুলস", emoji = "🔨", sortOrder = 1, colorHex = "#10B981"),
                Category(nameEn = "Power Tools", nameBn = "পাওয়ার টুলস", emoji = "🪚", sortOrder = 2, colorHex = "#F59E0B"),
                Category(nameEn = "Nuts Bolts & Screws", nameBn = "নাট বল্টু ও স্ক্রু", emoji = "🔩", sortOrder = 3, colorHex = "#64748B"),
                Category(nameEn = "Electrical Fittings", nameBn = "ইলেকট্রিক্যাল ফিটিংস", emoji = "🔌", sortOrder = 4, colorHex = "#3B82F6"),
                Category(nameEn = "Wire & Cable", nameBn = "ওয়্যার ও কেবল", emoji = "🧵", sortOrder = 5, colorHex = "#8B5CF6"),
                Category(nameEn = "Lights & Bulbs", nameBn = "লাইট ও বাল্ব", emoji = "💡", sortOrder = 6, colorHex = "#F97316"),
                Category(nameEn = "Pipes & Fittings", nameBn = "পাইপ ও ফিটিংস", emoji = "🚰", sortOrder = 7, colorHex = "#06B6D4"),
                Category(nameEn = "Sanitary & Bathroom", nameBn = "স্যানিটারি ও বাথরুম", emoji = "🚿", sortOrder = 8, colorHex = "#14B8A6"),
                Category(nameEn = "Paint & Chemicals", nameBn = "পেইন্ট ও কেমিক্যাল", emoji = "🎨", sortOrder = 9, colorHex = "#EC4899"),
                Category(nameEn = "Adhesives & Sealants", nameBn = "আঠা ও সিল্যান্ট", emoji = "🧴", sortOrder = 10, colorHex = "#EAB308"),
                Category(nameEn = "Locks & Door Fittings", nameBn = "তালা ও ডোর ফিটিংস", emoji = "🔒", sortOrder = 11, colorHex = "#6366F1"),
                Category(nameEn = "Building Materials", nameBn = "নির্মাণ সামগ্রী", emoji = "🧱", sortOrder = 12, colorHex = "#EF4444"),
                Category(nameEn = "Cutting & Abrasives", nameBn = "কাটিং ও ডিস্ক", emoji = "⚙️", sortOrder = 13, colorHex = "#78716C"),
                Category(nameEn = "Safety Equipment", nameBn = "নিরাপত্তা সামগ্রী", emoji = "🥽", sortOrder = 14, colorHex = "#10B981"),
                Category(nameEn = "Fans & Appliances", nameBn = "ফ্যান ও অ্যাপ্লায়েন্স", emoji = "🌀", sortOrder = 15, colorHex = "#0284C7"),
                Category(nameEn = "Other", nameBn = "অন্যান্য", emoji = "📦", sortOrder = 16, colorHex = "#94A3B8")
            )
            categoryDao.insertAll(defaultCategories)
        }

        // 2. Seed Customers if empty
        if (customerDao.getCustomerCount() == 0) {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L
            val defaultCustomers = listOf(
                Customer(
                    name = "করিম বিল্ডার্স (Karim Builders)",
                    phone = "01711-223344",
                    address = "মতিঝিল বাণিজ্যিক এলাকা, ঢাকা",
                    note = "বড় কনস্ট্রাকশন ঠিকাদার",
                    totalBaki = 3500.0,
                    dueDate = now + (dayMs * 5), // Due date in future -> Not overdue
                    lastPaymentDate = now - (dayMs * 1),
                    lastTransactionTime = now - (dayMs * 1)
                ),
                Customer(
                    name = "হাসান ট্রেডার্স (Hasan Traders)",
                    phone = "01822-334455",
                    address = "মিরপুর-১০, ঢাকা",
                    note = "নিয়মিত স্যানিটারি সামগ্রী ক্রয়, চেক বা নগদ দেন",
                    totalBaki = 1400.0,
                    dueDate = now - (dayMs * 2), // Due date passed 2 days ago -> Overdue!
                    lastTransactionTime = now - (dayMs * 5)
                ),
                Customer(
                    name = "আবুল কালাম ঠিকাদার (Abul Kalam)",
                    phone = "01933-445566",
                    address = "সেক্টর ৪, উত্তরা, ঢাকা",
                    note = "বিল্ডিং সাইটের জন্য রড পেরেক নেন",
                    totalBaki = 1000.0,
                    dueDate = null, // No due date
                    lastPaymentDate = null,
                    lastTransactionTime = now - (dayMs * 9) // No payment for 9 days -> Overdue!
                ),
                Customer(
                    name = "জামাল মেটাল ওয়ার্কস (Jamal Metal)",
                    phone = "01555-667788",
                    address = "টঙ্গী বিসিক শিল্পনগরী",
                    note = "নগদ লেনদেনকারী, হিসাব পরিশোধিত",
                    totalBaki = 0.0,
                    lastTransactionTime = now - (dayMs * 12)
                )
            )
            customerDao.insertAll(defaultCustomers)
        }

        // 3. Seed Products matching reference image if empty
        if (productDao.getProductCount() == 0) {
            val starterProducts = listOf(
                Product(
                    nameBn = "ক্ল হ্যামার কাঠের বাটসহ",
                    nameEn = "Claw Hammer",
                    category = "Hand Tools",
                    description = "ভারী কাজের জন্য স্টিল হাতুড়ি কাঠের বাটসহ",
                    photoKey = "hammer",
                    buyPrice = 350.0,
                    sellPrice = 450.0,
                    stockQuantity = 4, // Low stock
                    lowStockThreshold = 5,
                    unitBn = "টি",
                    unitEn = "pcs"
                ),
                Product(
                    nameBn = "পিভিসি পাইপ ১\" ১০ ফুট",
                    nameEn = "PVC Pipe 1\" 10ft",
                    category = "Pipes & Fittings",
                    description = "উচ্চ গ্রেড ড্রেনেজ ও প্লাম্বিং পাইপ",
                    photoKey = "pvc_pipe",
                    buyPrice = 160.0,
                    sellPrice = 220.0,
                    stockQuantity = 25,
                    lowStockThreshold = 5,
                    unitBn = "টি",
                    unitEn = "pcs"
                ),
                Product(
                    nameBn = "পেইন্ট বাকেট (৪লি)",
                    nameEn = "Paint Bucket",
                    category = "Paint & Chemicals",
                    description = "প্রিমিয়াম কোয়ালিটি দেয়ালের সাদা রং",
                    photoKey = "paint",
                    buyPrice = 750.0,
                    sellPrice = 950.0,
                    stockQuantity = 15,
                    lowStockThreshold = 5,
                    unitBn = "ক্যান",
                    unitEn = "cans"
                ),
                Product(
                    nameBn = "ইলেকট্রিক্যাল পিয়ানো সুইচ",
                    nameEn = "Electrical Switch",
                    category = "Electrical Fittings",
                    description = "মডুলার ডাবল গ্যাং ওয়াল সুইচ",
                    photoKey = "switch",
                    buyPrice = 60.0,
                    sellPrice = 90.0,
                    stockQuantity = 8,
                    lowStockThreshold = 8,
                    unitBn = "টি",
                    unitEn = "pcs"
                ),
                Product(
                    nameBn = "পাওয়ার ড্রিল মেশিন",
                    nameEn = "Power Drill",
                    category = "Power Tools",
                    description = "১৩মিমি ইমপ্যাক্ট হেভি ডিউটি ড্রিল মেশিন",
                    photoKey = "drill",
                    buyPrice = 1800.0,
                    sellPrice = 2400.0,
                    stockQuantity = 12,
                    lowStockThreshold = 4,
                    unitBn = "টি",
                    unitEn = "pcs"
                ),
                Product(
                    nameBn = "ব্রাস প্যাডলক (৫০মিমি)",
                    nameEn = "Brass Lock",
                    category = "Locks & Door Fittings",
                    description = "নিরাপদ পিতলের তালা ৪টি চাবিসহ",
                    photoKey = "lock",
                    buyPrice = 290.0,
                    sellPrice = 380.0,
                    stockQuantity = 10,
                    lowStockThreshold = 5,
                    unitBn = "টি",
                    unitEn = "pcs"
                ),
                Product(
                    nameBn = "স্টিল পেরেক ২ ইঞ্চি",
                    nameEn = "Steel Nails 2 inch",
                    category = "Nuts Bolts & Screws",
                    description = "গ্যালভানাইজড স্টিল হার্ডওয়্যার পেরেক",
                    photoKey = "nails",
                    buyPrice = 120.0,
                    sellPrice = 160.0,
                    stockQuantity = 20,
                    lowStockThreshold = 10,
                    unitBn = "কেজি",
                    unitEn = "kg"
                )
            )
            productDao.insertAll(starterProducts)
        }

        // 4. Seed Sales if empty
        if (saleOrderDao.getSalesCount() == 0) {
            val now = System.currentTimeMillis()
            val hourMs = 3600000L
            val starterSales = listOf(
                SaleOrder(
                    invoiceNumber = "INV-1001",
                    timestamp = now - (hourMs * 1),
                    customerId = 1,
                    customerName = "করিম বিল্ডার্স (Karim Builders)",
                    customerPhone = "01711223344",
                    totalAmount = 1050.0,
                    paidAmount = 1050.0,
                    dueAmount = 0.0,
                    totalCost = 800.0,
                    totalItemsCount = 3,
                    summaryItems = "ক্ল হ্যামার, পিভিসি পাইপ"
                ),
                SaleOrder(
                    invoiceNumber = "INV-1002",
                    timestamp = now - (hourMs * 26),
                    customerId = 2,
                    customerName = "হাসান ট্রেডার্স (Hasan Traders)",
                    customerPhone = "01822334455",
                    totalAmount = 2400.0,
                    paidAmount = 1000.0,
                    dueAmount = 1400.0,
                    totalCost = 1800.0,
                    totalItemsCount = 6,
                    summaryItems = "পেইন্ট বাকেট, সুইচ",
                    isCredit = true
                ),
                SaleOrder(
                    invoiceNumber = "INV-1003",
                    timestamp = now - (hourMs * 48),
                    customerId = 3,
                    customerName = "আবুল কালাম ঠিকাদার (Abul Kalam)",
                    customerPhone = "01933445566",
                    totalAmount = 3000.0,
                    paidAmount = 2000.0,
                    dueAmount = 1000.0,
                    totalCost = 2200.0,
                    totalItemsCount = 4,
                    summaryItems = "ব্রাস লক, ড্রিল",
                    isCredit = true
                )
            )
            saleOrderDao.insertAll(starterSales)
        }

        // 5. Seed Purchases if empty
        if (purchaseDao.getPurchaseCount() == 0) {
            val now = System.currentTimeMillis()
            val hourMs = 3600000L
            val starterPurchases = listOf(
                PurchaseRecord(
                    productId = 6,
                    productName = "Brass Lock",
                    quantity = 50,
                    unitBuyingPrice = 480.0,
                    totalCost = 24000.0,
                    timestamp = now - (hourMs * 2), // 10:15 AM today
                    supplierName = "মেঘনা হার্ডওয়্যার ও মেটাল (Meghna Hardware)",
                    supplierId = 1,
                    paidAmount = 14000.0,
                    dueAmount = 10000.0,
                    note = "পাইকারি স্টক চালান #৮৮৪"
                ),
                PurchaseRecord(
                    productId = 2,
                    productName = "PVC Pipe 2\" 10ft",
                    quantity = 60,
                    unitBuyingPrice = 160.0,
                    totalCost = 9600.0,
                    timestamp = now - (hourMs * 28), // Yesterday
                    supplierName = "ন্যাশনাল পলিমার ইন্ডাঃ (National Polymer)",
                    supplierId = 2,
                    paidAmount = 5000.0,
                    dueAmount = 4600.0,
                    note = "ড্রেনেজ ও পানির পাইপ চালান #৫২২"
                ),
                PurchaseRecord(
                    productId = 3,
                    productName = "Latex Paint",
                    quantity = 25,
                    unitBuyingPrice = 750.0,
                    totalCost = 18750.0,
                    timestamp = now - (hourMs * 72), // 3 days ago
                    supplierName = "এশিয়ান পেইন্টস ডিলারশিপ (Asian Paints)",
                    supplierId = 3,
                    paidAmount = 10000.0,
                    dueAmount = 8750.0,
                    note = "সাদা ওয়াল পেইন্ট বাকেট"
                )
            )
            purchaseDao.insertAll(starterPurchases)
        }

        // 6. Seed Suppliers if empty
        if (supplierDao != null && supplierDao.getSupplierCount() == 0) {
            val starterSuppliers = listOf(
                Supplier(
                    id = 1,
                    name = "মোঃ রফিকুল ইসলাম (Rafiqul Islam)",
                    company = "মেঘনা হার্ডওয়্যার ও মেটাল (Meghna Hardware)",
                    phone = "01712-345678",
                    address = "দোকান #১২, নবাবপুর রোড, ঢাকা",
                    suppliedCategories = "Tools, Locks & Fittings, Screws",
                    note = "প্রধান পাইকারি ডিস্ট্রিবিউটর, নিয়মিত সরবরাহকারী",
                    amountOwed = 48500.0
                ),
                Supplier(
                    id = 2,
                    name = "ইঞ্জিঃ সারোয়ার হোসেন (Sarwar Hossain)",
                    company = "ন্যাশনাল পলিমার ইন্ডাঃ (National Polymer)",
                    phone = "01819-876543",
                    address = "প্লট #৪৫, তেজগাঁও শিল্প এলাকা, ঢাকা",
                    suppliedCategories = "Pipes & Plumbing, Sanitary",
                    note = "ক্রেডিট সুবিধা: ১৫ দিন, সকল সাইজের পিভিসি পাইপ",
                    amountOwed = 29600.0
                ),
                Supplier(
                    id = 3,
                    name = "আলমগীর কবির (Alamgir Kabir)",
                    company = "এশিয়ান পেইন্টস ডিলারশিপ (Asian Paints)",
                    phone = "01912-987654",
                    address = "রোড #৭, মহাখালী বাণিজ্যিক এলাকা, ঢাকা",
                    suppliedCategories = "Paints & Wall Finishes",
                    note = "১০ কার্টনের বেশি অর্ডারে ৫% নগদ ক্যাশব্যাক",
                    amountOwed = 15200.0
                ),
                Supplier(
                    id = 4,
                    name = "হাজী দেলোয়ার হোসেন (Delwar Hossain)",
                    company = "সুপার স্টার ইলেকট্রিক (SSG)",
                    phone = "01611-223388",
                    address = "সুন্দরবন স্কয়ার মার্কেট, গুলিস্তান, ঢাকা",
                    suppliedCategories = "Electrical & Lighting",
                    note = "তার, সুইচ বোর্ড ও এলইডি বাতি",
                    amountOwed = 0.0
                )
            )
            supplierDao.insertAll(starterSuppliers)

            val now = System.currentTimeMillis()
            val dayMs = 86400000L
            val starterPayments = listOf(
                SupplierPayment(
                    supplierId = 1,
                    amount = 20000.0,
                    paymentMethod = "Bank Transfer",
                    note = "পূবালী ব্যাংক চেক নং #৮৪২১০২",
                    timestamp = now - (dayMs * 2)
                ),
                SupplierPayment(
                    supplierId = 1,
                    amount = 15000.0,
                    paymentMethod = "Cash",
                    note = "নগদ পরিশোধ চালান #৮৯২",
                    timestamp = now - (dayMs * 7)
                ),
                SupplierPayment(
                    supplierId = 2,
                    amount = 10000.0,
                    paymentMethod = "bKash Merchant",
                    note = "মার্চেন্ট পেমেন্ট TrxID: 9X48LK02",
                    timestamp = now - (dayMs * 4)
                )
            )
            supplierPaymentDao?.insertAll(starterPayments)
        }

        // 7. Seed Expenses if empty
        if (expenseDao != null && expenseDao.getExpensesCount() == 0) {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L
            val starterExpenses = listOf(
                Expense(
                    title = "দোকানের বিদ্যুৎ বিল (Electricity Bill)",
                    amount = 3200.0,
                    category = "Electricity",
                    timestamp = now - (dayMs * 2),
                    note = "ডেসকো বিল - সেপ্টেম্বর"
                ),
                Expense(
                    title = "দোকান কর্মচারীর সাপ্তাহিক বেতন (Staff Salary)",
                    amount = 6000.0,
                    category = "Staff",
                    timestamp = now - (dayMs * 5),
                    note = "সহকারী কর্মচারীর বেতন"
                ),
                Expense(
                    title = "মাল পরিবহন ও ভ্যান ভাড়া (Transport)",
                    amount = 1200.0,
                    category = "Transport",
                    timestamp = now - (dayMs * 1),
                    note = "নবাবপুর থেকে মালামাল আনয়ন"
                ),
                Expense(
                    title = "দোকানের চা-নাস্তা ও মেহমানদারি (Tea & Refreshments)",
                    amount = 450.0,
                    category = "Tea/Snacks",
                    timestamp = now,
                    note = "গ্রাহক ও কর্মচারীদের চা"
                ),
                Expense(
                    title = "দোকান মেরামত ও তাক সংস্কার (Shop Repair)",
                    amount = 1500.0,
                    category = "Repair",
                    timestamp = now - (dayMs * 12),
                    note = "র‍্যাক ও শেলফ মেরামত"
                )
            )
            expenseDao.insertAll(starterExpenses)
        }
    }

    suspend fun insertExpense(expense: Expense): Long = withContext(Dispatchers.IO) {
        expenseDao?.insertExpense(expense) ?: 0L
    }

    suspend fun updateExpense(expense: Expense) = withContext(Dispatchers.IO) {
        expenseDao?.updateExpense(expense)
    }

    suspend fun deleteExpense(expenseId: Long) = withContext(Dispatchers.IO) {
        expenseDao?.deleteExpenseById(expenseId)
    }
}

