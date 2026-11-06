package com.example

import com.example.data.local.entity.Product
import com.example.data.local.entity.PurchaseRecord
import com.example.data.local.entity.SaleOrder
import com.example.data.local.entity.Supplier
import com.example.data.local.entity.SupplierPayment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUnitTest {

  @Test
  fun testProductLocalizationAndStock() {
    val product = Product(
      id = 1,
      nameBn = "হাতুড়ি",
      nameEn = "Claw Hammer",
      category = "Tools",
      buyPrice = 150.0,
      sellPrice = 220.0,
      stockQuantity = 4,
      lowStockThreshold = 5
    )
    assertEquals("হাতুড়ি", product.localizedName(isBangla = true))
    assertEquals("Claw Hammer", product.localizedName(isBangla = false))
    assertEquals("টি", product.localizedUnit(isBangla = true))
    assertEquals("pcs", product.localizedUnit(isBangla = false))
    assertTrue(product.isLowStock)
  }

  @Test
  fun testSaleOrderProfitCalculation() {
    val order = SaleOrder(
      id = 1,
      invoiceNumber = "INV-1001",
      customerName = "Rahim Mia",
      totalAmount = 500.0,
      paidAmount = 400.0,
      dueAmount = 100.0,
      totalCost = 350.0,
      totalItemsCount = 2,
      isCredit = true
    )
    assertEquals(500.0, order.totalAmount, 0.001)
    assertEquals(150.0, order.profit, 0.001)
    assertEquals(100.0, order.dueAmount, 0.001)
    assertTrue(order.isCredit)
  }

  @Test
  fun testSupplierOwedAndPaymentTracking() {
    val supplier = Supplier(
      id = 1,
      name = "Rafiqul Islam",
      company = "Meghna Hardware",
      phone = "01712-345678",
      address = "Nawabpur Road, Dhaka",
      suppliedCategories = "Tools, Locks, Screws",
      note = "Primary distributor",
      amountOwed = 48500.0
    )
    assertEquals("Meghna Hardware", supplier.company)
    assertEquals(48500.0, supplier.amountOwed, 0.001)

    // Simulate adding purchase with unpaid due
    val totalCost = 10000.0
    val paidNow = 4000.0
    val duePart = totalCost - paidNow
    val updatedOwed = supplier.amountOwed + duePart
    assertEquals(54500.0, updatedOwed, 0.001)

    // Simulate recording payment to supplier
    val payment = SupplierPayment(
      id = 1,
      supplierId = 1,
      amount = 20000.0,
      paymentMethod = "Bank Transfer",
      note = "Cheque #884"
    )
    val remainingOwed = updatedOwed - payment.amount
    assertEquals(34500.0, remainingOwed, 0.001)
  }

  @Test
  fun testPurchaseRecordWithSupplierAndBillPhoto() {
    val purchase = PurchaseRecord(
      id = 10,
      productId = 2,
      productName = "PVC Pipe 2 inch",
      quantity = 50,
      unitBuyingPrice = 160.0,
      totalCost = 8000.0,
      supplierId = 2,
      supplierName = "National Polymer",
      paidAmount = 5000.0,
      dueAmount = 3000.0,
      billPhotoUri = "content://media/external/images/media/42",
      note = "Batch #522"
    )
    assertEquals(2L, purchase.supplierId)
    assertEquals(3000.0, purchase.dueAmount, 0.001)
    assertEquals("content://media/external/images/media/42", purchase.billPhotoUri)
  }

  @Test
  fun testCustomerOverdueDetection() {
    val now = System.currentTimeMillis()
    val dayMs = 24L * 60 * 60 * 1000L

    // Case 1: Balance is 0 -> Not overdue
    val zeroBalanceCustomer = com.example.data.local.entity.Customer(
      id = 1,
      name = "Jamal Metal",
      phone = "01811-223344",
      totalBaki = 0.0,
      dueDate = now - 2 * dayMs
    )
    assertTrue(!com.example.ui.screens.baki.isCustomerOverdue(zeroBalanceCustomer))

    // Case 2: Due date is set and has passed -> Overdue
    val overdueByDueDateCustomer = com.example.data.local.entity.Customer(
      id = 2,
      name = "Hasan Traders",
      phone = "01811-001122",
      totalBaki = 14500.0,
      dueDate = now - 2 * dayMs,
      lastPaymentDate = now - 1 * dayMs
    )
    assertTrue(com.example.ui.screens.baki.isCustomerOverdue(overdueByDueDateCustomer))

    // Case 3: Due date is set in the future -> Not overdue
    val notOverdueDueDateCustomer = com.example.data.local.entity.Customer(
      id = 3,
      name = "Karim Builders",
      phone = "01711-223344",
      totalBaki = 28500.0,
      dueDate = now + 5 * dayMs,
      lastPaymentDate = now - 3 * dayMs
    )
    assertTrue(!com.example.ui.screens.baki.isCustomerOverdue(notOverdueDueDateCustomer))

    // Case 4: No due date, last payment was 8 days ago (>7 days) -> Overdue
    val overdueNoDueDatePaid8DaysAgo = com.example.data.local.entity.Customer(
      id = 4,
      name = "Abul Kalam",
      phone = "01911-334455",
      totalBaki = 9200.0,
      dueDate = null,
      lastPaymentDate = now - 8 * dayMs
    )
    assertTrue(com.example.ui.screens.baki.isCustomerOverdue(overdueNoDueDatePaid8DaysAgo))

    // Case 5: No due date, last payment was 3 days ago (<=7 days) -> Not overdue
    val activeCustomerPaidRecently = com.example.data.local.entity.Customer(
      id = 5,
      name = "Tareq Enterprise",
      phone = "01511-998877",
      totalBaki = 5000.0,
      dueDate = null,
      lastPaymentDate = now - 3 * dayMs
    )
    assertTrue(!com.example.ui.screens.baki.isCustomerOverdue(activeCustomerPaidRecently))
  }

  @Test
  fun testCustomerLedgerBalanceRecalculation() {
    val totalCredit = 15000.0
    val payment1 = 3000.0
    val payment2 = 5000.0

    val balanceAfterPayments = totalCredit - (payment1 + payment2)
    assertEquals(7000.0, balanceAfterPayments, 0.001)

    // Simulate editing payment 1 from 3000 to 4500
    val editedPayment1 = 4500.0
    val recalculatedBalance = totalCredit - (editedPayment1 + payment2)
    assertEquals(5500.0, recalculatedBalance, 0.001)
  }

  @Test
  fun testExpenseAndNetMarginCalculations() {
    val expense1 = com.example.data.local.entity.Expense(
      id = 1,
      title = "Electricity",
      amount = 3200.0,
      category = "Electricity"
    )
    val expense2 = com.example.data.local.entity.Expense(
      id = 2,
      title = "Staff Salary",
      amount = 6000.0,
      category = "Staff"
    )

    val totalExpenses = expense1.amount + expense2.amount
    assertEquals(9200.0, totalExpenses, 0.001)

    val grossProfit = 31200.0
    val netMargin = grossProfit - totalExpenses
    assertEquals(22000.0, netMargin, 0.001)
  }

  @Test
  fun testDataHealthProductStockDiscrepancy() {
    val product = Product(
      id = 10,
      nameBn = "ব্রাস লক",
      nameEn = "Brass Lock",
      category = "Locks",
      buyPrice = 290.0,
      sellPrice = 380.0,
      stockQuantity = 10,
      unitBn = "টি",
      unitEn = "pcs"
    )
    val totalPurchased = 50
    val totalSold = 1
    val expectedStock = totalPurchased - totalSold // 49
    val diff = product.stockQuantity - expectedStock // 10 - 49 = -39

    val issue = com.example.ui.screens.business.DataHealthIssue.ProductStockIssue(
      product = product,
      currentStock = product.stockQuantity,
      expectedStock = expectedStock,
      diff = diff,
      totalPurchased = totalPurchased,
      totalSold = totalSold
    )

    assertEquals("product_10", issue.key)
    assertEquals(10, issue.currentStock)
    assertEquals(49, issue.expectedStock)
    assertEquals("-39", issue.differenceFormatted)
    assertTrue(!issue.isOver)
  }

  @Test
  fun testDataHealthCustomerBalanceDiscrepancy() {
    val customer = com.example.data.local.entity.Customer(
      id = 5,
      name = "করিম বিল্ডার্স",
      phone = "01711-223344",
      totalBaki = 3500.0
    )
    val totalDueSales = 0.0
    val totalPaid = 0.0
    val expectedBaki = 0.0
    val diff = customer.totalBaki - expectedBaki // +3500

    val issue = com.example.ui.screens.business.DataHealthIssue.CustomerBalanceIssue(
      customer = customer,
      currentBaki = customer.totalBaki,
      expectedBaki = expectedBaki,
      diff = diff,
      totalDueSales = totalDueSales,
      totalPaid = totalPaid
    )

    assertEquals("customer_5", issue.key)
    assertEquals(3500.0, issue.currentBaki, 0.001)
    assertEquals(0.0, issue.expectedBaki, 0.001)
    assertEquals("+৳3500", issue.differenceFormatted)
    assertTrue(issue.isOver)
  }

  @Test
  fun testDataHealthSupplierBalanceDiscrepancy() {
    val supplier = Supplier(
      id = 1,
      name = "রফিকুল ইসলাম",
      company = "মেঘনা হার্ডওয়্যার",
      amountOwed = 48500.0
    )
    val totalDuePurchases = 10000.0
    val totalPaid = 35000.0
    val expectedOwed = (totalDuePurchases - totalPaid).coerceAtLeast(0.0) // 0.0
    val diff = supplier.amountOwed - expectedOwed // +48500

    val issue = com.example.ui.screens.business.DataHealthIssue.SupplierBalanceIssue(
      supplier = supplier,
      currentOwed = supplier.amountOwed,
      expectedOwed = expectedOwed,
      diff = diff,
      totalDuePurchases = totalDuePurchases,
      totalPaid = totalPaid
    )

    assertEquals("supplier_1", issue.key)
    assertEquals(48500.0, issue.currentOwed, 0.001)
    assertEquals(0.0, issue.expectedOwed, 0.001)
    assertEquals("+৳48500", issue.differenceFormatted)
    assertTrue(issue.isOver)
  }
}


