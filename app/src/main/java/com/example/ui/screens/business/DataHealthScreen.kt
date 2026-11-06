package com.example.ui.screens.business

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.Customer
import com.example.data.local.entity.Product
import com.example.data.local.entity.Supplier
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.formatTaka
import com.example.ui.localization.toBengaliNumerals
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AppTypographyScale
import com.example.ui.theme.DarkSlateBackground
import com.example.ui.theme.DarkSlateCardBorder
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceVariant
import com.example.ui.theme.LowStockRed
import com.example.ui.theme.LowStockRedContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VividEmeraldAccent
import com.example.ui.theme.VividEmeraldContainer
import com.example.ui.theme.VividEmeraldPrimary
import com.example.ui.theme.WarmAmber

sealed class DataHealthIssue {
    abstract val key: String
    abstract val categoryNameEn: String
    abstract val categoryNameBn: String
    abstract val title: String
    abstract val subtitle: String
    abstract val currentValFormatted: String
    abstract val expectedValFormatted: String
    abstract val differenceFormatted: String
    abstract val isOver: Boolean
    abstract val explanationBn: String
    abstract val explanationEn: String

    data class ProductStockIssue(
        val product: Product,
        val currentStock: Int,
        val expectedStock: Int,
        val diff: Int,
        val totalPurchased: Int,
        val totalSold: Int
    ) : DataHealthIssue() {
        override val key: String = "product_${product.id}"
        override val categoryNameEn: String = "Stock Quantity"
        override val categoryNameBn: String = "পণ্য স্টক"
        override val title: String = product.nameBn.ifBlank { product.nameEn }
        override val subtitle: String = "${product.nameEn} • ${product.category}"
        override val currentValFormatted: String = "$currentStock ${product.unitBn}"
        override val expectedValFormatted: String = "$expectedStock ${product.unitBn}"
        override val differenceFormatted: String = if (diff > 0) "+$diff" else "$diff"
        override val isOver: Boolean = diff > 0
        override val explanationBn: String = "ক্রয় চালান মোট: $totalPurchased, বিক্রি চালান মোট: $totalSold। হিসাব অনুযায়ী স্টক হওয়া উচিত: $expectedStock ${product.unitBn} (বর্তমান রেকর্ড: $currentStock)"
        override val explanationEn: String = "Total purchased: $totalPurchased, Total sold: $totalSold. History says stock should be: $expectedStock ${product.unitEn} (Recorded: $currentStock)"
    }

    data class CustomerBalanceIssue(
        val customer: Customer,
        val currentBaki: Double,
        val expectedBaki: Double,
        val diff: Double,
        val totalDueSales: Double,
        val totalPaid: Double
    ) : DataHealthIssue() {
        override val key: String = "customer_${customer.id}"
        override val categoryNameEn: String = "Customer Due (Baki)"
        override val categoryNameBn: String = "কাস্টমার বাকি"
        override val title: String = customer.name
        override val subtitle: String = if (customer.phone.isNotBlank()) customer.phone else "Customer Ledger"
        override val currentValFormatted: String = "৳${currentBaki.toLong()}"
        override val expectedValFormatted: String = "৳${expectedBaki.toLong()}"
        override val differenceFormatted: String = if (diff > 0) "+৳${diff.toLong()}" else "-৳${(-diff).toLong()}"
        override val isOver: Boolean = diff > 0
        override val explanationBn: String = "বাকিতে বিক্রির মোট পাওনা: ৳${totalDueSales.toLong()}, মোট জমা/পরিশোধ: ৳${totalPaid.toLong()}। হিসাব অনুযায়ী বাকি হওয়া উচিত: ৳${expectedBaki.toLong()} (বর্তমান ব্যালেন্স: ৳${currentBaki.toLong()})"
        override val explanationEn: String = "Credit sales due: ৳${totalDueSales.toLong()}, Payments received: ৳${totalPaid.toLong()}. Entries say balance should be: ৳${expectedBaki.toLong()} (Recorded: ৳${currentBaki.toLong()})"
    }

    data class SupplierBalanceIssue(
        val supplier: Supplier,
        val currentOwed: Double,
        val expectedOwed: Double,
        val diff: Double,
        val totalDuePurchases: Double,
        val totalPaid: Double
    ) : DataHealthIssue() {
        override val key: String = "supplier_${supplier.id}"
        override val categoryNameEn: String = "Supplier Owed"
        override val categoryNameBn: String = "মহাজন দেনা"
        override val title: String = supplier.name
        override val subtitle: String = if (supplier.company.isNotBlank()) supplier.company else "Supplier Ledger"
        override val currentValFormatted: String = "৳${currentOwed.toLong()}"
        override val expectedValFormatted: String = "৳${expectedOwed.toLong()}"
        override val differenceFormatted: String = if (diff > 0) "+৳${diff.toLong()}" else "-৳${(-diff).toLong()}"
        override val isOver: Boolean = diff > 0
        override val explanationBn: String = "বাকি চালান মোট: ৳${totalDuePurchases.toLong()}, মোট পরিশোধ: ৳${totalPaid.toLong()}। হিসাব অনুযায়ী দেনা হওয়া উচিত: ৳${expectedOwed.toLong()} (বর্তমান ব্যালেন্স: ৳${currentOwed.toLong()})"
        override val explanationEn: String = "Purchases due: ৳${totalDuePurchases.toLong()}, Paid to supplier: ৳${totalPaid.toLong()}. Entries say balance should be: ৳${expectedOwed.toLong()} (Recorded: ৳${currentOwed.toLong()})"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataHealthScreen(
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val allIssues by viewModel.dataHealthIssues.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val allSuppliers by viewModel.allSuppliers.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("all") }
    var issueToFix by remember { mutableStateOf<DataHealthIssue?>(null) }
    var showFixAllConfirmDialog by remember { mutableStateOf(false) }

    BackHandler {
        onBack()
    }

    val filteredIssues = remember(allIssues, selectedFilter) {
        when (selectedFilter) {
            "product" -> allIssues.filterIsInstance<DataHealthIssue.ProductStockIssue>()
            "customer" -> allIssues.filterIsInstance<DataHealthIssue.CustomerBalanceIssue>()
            "supplier" -> allIssues.filterIsInstance<DataHealthIssue.SupplierBalanceIssue>()
            else -> allIssues
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSlateBackground)
            .testTag("data_health_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "ডাটা স্বাস্থ্য পরীক্ষা (Data Health)" else "Data Health Check",
                        style = AppTypographyScale.screenTitle,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("data_health_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    if (allIssues.isNotEmpty()) {
                        TextButton(
                            onClick = { showFixAllConfirmDialog = true },
                            modifier = Modifier.testTag("btn_fix_all_issues")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = VividEmeraldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "সব ঠিক করুন" else "Fix All",
                                style = AppTypographyScale.buttonText,
                                color = VividEmeraldAccent
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSlateBackground)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Overview card
                item {
                    DataHealthOverviewCard(
                        totalIssues = allIssues.size,
                        totalProducts = allProducts.size,
                        totalCustomers = allCustomers.size,
                        totalSuppliers = allSuppliers.size,
                        isBangla = isBangla
                    )
                }

                // Filter chips row
                item {
                    DataHealthFilterChipsRow(
                        selectedFilter = selectedFilter,
                        onSelectFilter = { selectedFilter = it },
                        productIssuesCount = allIssues.count { it is DataHealthIssue.ProductStockIssue },
                        customerIssuesCount = allIssues.count { it is DataHealthIssue.CustomerBalanceIssue },
                        supplierIssuesCount = allIssues.count { it is DataHealthIssue.SupplierBalanceIssue },
                        totalCount = allIssues.size,
                        isBangla = isBangla
                    )
                }

                // Empty state if all data is healthy
                if (filteredIssues.isEmpty()) {
                    item {
                        DataHealthCleanStateCard(isBangla = isBangla)
                    }
                } else {
                    // Mismatches List
                    items(filteredIssues, key = { it.key }) { issue ->
                        DataHealthIssueCard(
                            issue = issue,
                            isBangla = isBangla,
                            onFix = { issueToFix = issue }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Confirmation Dialog for Single Fix
    issueToFix?.let { issue ->
        FixConfirmationDialog(
            issue = issue,
            isBangla = isBangla,
            onDismiss = { issueToFix = null },
            onConfirm = {
                viewModel.applyDataHealthFix(issue)
                Toast.makeText(
                    context,
                    if (isBangla) "${issue.title} সফলভাবে সংশোধন করা হয়েছে!" else "${issue.title} fixed successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                issueToFix = null
            }
        )
    }

    // Confirmation Dialog for Fix All
    if (showFixAllConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showFixAllConfirmDialog = false },
            containerColor = DarkSlateSurface,
            icon = {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = null,
                    tint = VividEmeraldAccent,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (isBangla) "সকল অমিল সংশোধন করবেন?" else "Fix All Mismatches?",
                    style = AppTypographyScale.screenTitle,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = if (isBangla)
                        "শনাক্তকৃত মোট ${allIssues.size}টি আইটেমের রেকর্ড (পণ্য স্টক, কাস্টমার বাকি ও মহাজন দেনা) ক্রয়-বিক্রয় ও ভাউচার ইতিহাস অনুযায়ী স্বয়ংক্রিয়ভাবে সংশোধন করা হবে।"
                    else
                        "All ${allIssues.size} detected discrepancies (product stocks, customer baki, and supplier owed balances) will be automatically adjusted to match entry history records.",
                    style = AppTypographyScale.bodySmall,
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.applyFixAll(allIssues)
                        Toast.makeText(
                            context,
                            if (isBangla) "সকল অমিল সফলভাবে সংশোধন করা হয়েছে!" else "All mismatches fixed successfully!",
                            Toast.LENGTH_LONG
                        ).show()
                        showFixAllConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                    modifier = Modifier.testTag("confirm_fix_all_button")
                ) {
                    Text(if (isBangla) "হ্যাঁ, সব ঠিক করুন" else "Yes, Fix All", color = TextPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFixAllConfirmDialog = false }) {
                    Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun DataHealthOverviewCard(
    totalIssues: Int,
    totalProducts: Int,
    totalCustomers: Int,
    totalSuppliers: Int,
    isBangla: Boolean
) {
    val isClean = totalIssues == 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isClean) DarkSlateSurface else DarkSlateSurface
        ),
        border = BorderStroke(
            1.dp,
            if (isClean) VividEmeraldAccent.copy(alpha = 0.5f) else WarmAmber.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isClean) VividEmeraldContainer else LowStockRedContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isClean) Icons.Default.VerifiedUser else Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = if (isClean) VividEmeraldAccent else WarmAmber,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isClean) {
                                if (isBangla) "ডাটা স্বাস্থ্য: ১০০% নির্ভুল" else "Data Health: 100% Healthy"
                            } else {
                                if (isBangla) "ডাটা স্বাস্থ্য: $totalIssues টি অমিল রয়েছে" else "Data Health: $totalIssues Mismatches"
                            },
                            style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold),
                            color = if (isClean) VividEmeraldAccent else WarmAmber
                        )
                        Text(
                            text = if (isBangla)
                                "পণ্য স্টক, কাস্টমার বাকি ও মহাজন দেনা যাচাইকরণ"
                            else
                                "Audit of stock levels, customer baki & supplier owed balances",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                }
            }

            HorizontalDivider(color = DarkSlateCardBorder)

            // Statistics checked
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AuditStatItem(
                    label = if (isBangla) "পণ্য স্টক" else "Products",
                    count = totalProducts,
                    isBangla = isBangla
                )
                AuditStatItem(
                    label = if (isBangla) "কাস্টমার খাতা" else "Customers",
                    count = totalCustomers,
                    isBangla = isBangla
                )
                AuditStatItem(
                    label = if (isBangla) "মহাজন খাতা" else "Suppliers",
                    count = totalSuppliers,
                    isBangla = isBangla
                )
            }
        }
    }
}

@Composable
private fun AuditStatItem(label: String, count: Int, isBangla: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (isBangla) "${toBengaliNumerals(count.toString())} টি" else count.toString(),
            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = label,
            style = AppTypographyScale.hintText,
            color = TextMuted
        )
    }
}

@Composable
private fun DataHealthFilterChipsRow(
    selectedFilter: String,
    onSelectFilter: (String) -> Unit,
    productIssuesCount: Int,
    customerIssuesCount: Int,
    supplierIssuesCount: Int,
    totalCount: Int,
    isBangla: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterPill(
            id = "all",
            label = if (isBangla) "সব অমিল ($totalCount)" else "All ($totalCount)",
            isSelected = selectedFilter == "all",
            onClick = { onSelectFilter("all") }
        )
        FilterPill(
            id = "product",
            label = if (isBangla) "পণ্য স্টক ($productIssuesCount)" else "Stocks ($productIssuesCount)",
            isSelected = selectedFilter == "product",
            onClick = { onSelectFilter("product") }
        )
        FilterPill(
            id = "customer",
            label = if (isBangla) "কাস্টমার বাকি ($customerIssuesCount)" else "Customers ($customerIssuesCount)",
            isSelected = selectedFilter == "customer",
            onClick = { onSelectFilter("customer") }
        )
        FilterPill(
            id = "supplier",
            label = if (isBangla) "মহাজন দেনা ($supplierIssuesCount)" else "Suppliers ($supplierIssuesCount)",
            isSelected = selectedFilter == "supplier",
            onClick = { onSelectFilter("supplier") }
        )
    }
}

@Composable
private fun FilterPill(
    id: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("health_filter_$id"),
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) VividEmeraldPrimary else DarkSlateSurfaceVariant,
        border = BorderStroke(1.dp, if (isSelected) VividEmeraldAccent else DarkSlateCardBorder)
    ) {
        Text(
            text = label,
            style = AppTypographyScale.hintText.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) Color.White else TextMuted,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun DataHealthCleanStateCard(isBangla: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, VividEmeraldAccent.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(VividEmeraldContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = VividEmeraldAccent,
                    modifier = Modifier.size(34.dp)
                )
            }

            Text(
                text = if (isBangla) "সকল হিসাব ১০০% সঠিক আছে!" else "All Data is 100% Consistent!",
                style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold),
                color = VividEmeraldAccent,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isBangla)
                    "আপনার দোকানের সমস্ত পণ্যের স্টক এবং কাস্টমার ও মহাজনদের দেনা-পাওনার ব্যালেন্স মূল চালান, বিক্রি ও ভাউচার রেকর্ডের সাথে সম্পূর্ণ মিলে গেছে। কোনো অমিল পাওয়া যায়নি।"
                else
                    "All product stocks, customer baki balances, and supplier balances perfectly match their recorded purchase invoices, sales orders, and payment vouchers.",
                style = AppTypographyScale.hintText,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DataHealthIssueCard(
    issue: DataHealthIssue,
    isBangla: Boolean,
    onFix: () -> Unit
) {
    val categoryBadgeColor = when (issue) {
        is DataHealthIssue.ProductStockIssue -> Color(0xFF06B6D4) // Cyan
        is DataHealthIssue.CustomerBalanceIssue -> Color(0xFFF59E0B) // Amber
        is DataHealthIssue.SupplierBalanceIssue -> Color(0xFFEF4444) // Red
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("health_issue_${issue.key}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Category Badge on left, Difference on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = categoryBadgeColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, categoryBadgeColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = if (isBangla) issue.categoryNameBn else issue.categoryNameEn,
                        style = AppTypographyScale.hintText.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                        color = categoryBadgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Discrepancy pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (issue.isOver) LowStockRedContainer else Color(0x22F59E0B)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isBangla) "পার্থক্য: ${issue.differenceFormatted}" else "Diff: ${issue.differenceFormatted}",
                            style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                            color = if (issue.isOver) LowStockRed else WarmAmber
                        )
                    }
                }
            }

            // Title & Subtitle
            Column {
                Text(
                    text = issue.title,
                    style = AppTypographyScale.cardTitle.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = issue.subtitle,
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )
            }

            // Comparison Metrics Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = DarkSlateBackground,
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBangla) "বর্তমান রেকর্ড" else "Recorded Now",
                            style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                            color = TextMuted
                        )
                        Text(
                            text = issue.currentValFormatted,
                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Mismatch",
                        tint = WarmAmber,
                        modifier = Modifier.size(18.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBangla) "চালান/ভাউচার হিসাব" else "History Should Be",
                            style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                            color = TextMuted
                        )
                        Text(
                            text = issue.expectedValFormatted,
                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                            color = VividEmeraldAccent
                        )
                    }
                }
            }

            // Explanation note
            Text(
                text = if (isBangla) issue.explanationBn else issue.explanationEn,
                style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                color = TextMuted
            )

            // Fix Button
            Button(
                onClick = onFix,
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_fix_${issue.key}")
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla)
                        "সংশোধন করুন (${issue.expectedValFormatted} এ সেট করুন)"
                    else
                        "Fix Discrepancy (Set to ${issue.expectedValFormatted})",
                    style = AppTypographyScale.buttonText
                )
            }
        }
    }
}

@Composable
private fun FixConfirmationDialog(
    issue: DataHealthIssue,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = WarmAmber,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = if (isBangla) "সংশোধন নিশ্চিত করুন" else "Confirm Fix",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isBangla)
                        "আপনি কি নিশ্চিত যে '${issue.title}' এর রেকর্ড সংশোধন করে মূল ক্রয়-বিক্রয় ও ভাউচার হিসাবের সাথে সামঞ্জস্য করতে চান?"
                    else
                        "Are you sure you want to correct the recorded value for '${issue.title}' to match actual transaction entry history?",
                    style = AppTypographyScale.bodySmall,
                    color = TextPrimary
                )

                Surface(
                    color = DarkSlateBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DarkSlateCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "আইটেম:" else "Target:", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(text = issue.title, style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "বর্তমান রেকর্ড:" else "Current Value:", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(text = issue.currentValFormatted, style = AppTypographyScale.hintText, color = LowStockRed)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "নতুন সংশোধিত মান:" else "Corrected Value:", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(text = issue.expectedValFormatted, style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold), color = VividEmeraldAccent)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "পার্থক্য সমন্বয়:" else "Difference:", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(text = issue.differenceFormatted, style = AppTypographyScale.hintText, color = WarmAmber)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                modifier = Modifier.testTag("btn_confirm_dialog_fix")
            ) {
                Text(if (isBangla) "হ্যাঁ, সংশোধন করুন" else "Confirm & Fix", color = TextPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
            }
        }
    )
}
