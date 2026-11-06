package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.HardwareCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.formatTaka
import com.example.ui.localization.toBengaliNumerals
import com.example.ui.screens.business.AddExpenseDialog
import com.example.ui.screens.business.BusinessTimePeriod
import com.example.ui.screens.business.BusinessViewModel
import com.example.ui.screens.business.CategoryDonutChart
import com.example.ui.screens.business.DataHealthScreen
import com.example.ui.theme.WarmAmber
import com.example.ui.screens.business.ExpensesReportScreen
import com.example.ui.screens.business.HardwareThumbnailIconSmall
import com.example.ui.screens.business.LowStockReportScreen
import com.example.ui.screens.business.MoneyNowReportScreen
import com.example.ui.screens.business.MonthlyReportScreen
import com.example.ui.screens.business.OverviewReportScreen
import com.example.ui.screens.business.PeriodSelectorPills
import com.example.ui.screens.business.ProductRankingReportScreen
import com.example.ui.screens.business.SalesBarChartCanvas
import com.example.ui.screens.business.SalesByCategoryReportScreen
import com.example.ui.screens.business.SalesChartReportScreen
import com.example.ui.screens.business.StatCard
import com.example.ui.screens.business.SuppliersScreen
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
import com.example.ui.theme.VividEmeraldOnContainer
import com.example.ui.theme.VividEmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    currentLanguage: AppLanguage,
    onNavigateToBaki: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BusinessViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentView by viewModel.currentView.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA

    // State flows
    val period by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val metrics by viewModel.periodMetrics.collectAsStateWithLifecycle()
    val moneyNow by viewModel.moneyNowMetrics.collectAsStateWithLifecycle()
    val salesChartBars by viewModel.salesChartBars.collectAsStateWithLifecycle()
    val bestSellers by viewModel.bestSellers.collectAsStateWithLifecycle()
    val salesByCategory by viewModel.salesByCategory.collectAsStateWithLifecycle()
    val showAddExpenseDialog by viewModel.showAddExpenseDialog.collectAsStateWithLifecycle()
    val showCustomDatePicker by viewModel.showCustomDatePickerDialog.collectAsStateWithLifecycle()
    val dataHealthIssues by viewModel.dataHealthIssues.collectAsStateWithLifecycle()

    var isExportingPdf by remember { mutableStateOf(false) }

    // Dialog for adding expense
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { viewModel.closeAddExpenseDialog() },
            onSave = { title, amt, cat, note ->
                viewModel.addExpense(title, amt, cat, note)
            },
            currentLanguage = currentLanguage
        )
    }

    // Custom Date Range Picker Dialog
    if (showCustomDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.closeCustomDatePicker() },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis ?: System.currentTimeMillis()
                        if (start != null) {
                            viewModel.setCustomDateRange(start, end)
                        } else {
                            viewModel.closeCustomDatePicker()
                        }
                    }
                ) {
                    Text(if (isBangla) "ঠিক আছে" else "Confirm", color = VividEmeraldAccent, style = AppTypographyScale.buttonText)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeCustomDatePicker() }) {
                    Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted, style = AppTypographyScale.buttonText)
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        if (isBangla) "তারিখের সীমা নির্বাচন করুন" else "Select Custom Date Range",
                        style = AppTypographyScale.sectionHeading,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }

    // Render Sub-Report Screen or Main Dashboard
    when (currentView) {
        "report_overview" -> OverviewReportScreen(
            viewModel = viewModel,
            currentLanguage = currentLanguage,
            onBack = { viewModel.navigateToDashboard() }
        )
        "report_money_now" -> MoneyNowReportScreen(
            viewModel = viewModel,
            currentLanguage = currentLanguage,
            onBack = { viewModel.navigateToDashboard() }
        )
        "report_sales_chart" -> SalesChartReportScreen(
            viewModel = viewModel,
            currentLanguage = currentLanguage,
            onBack = { viewModel.navigateToDashboard() }
        )
        "report_best_sellers" -> ProductRankingReportScreen(
            viewModel = viewModel,
            isBestSellers = true,
            currentLanguage = currentLanguage,
            onBack = { viewModel.navigateToDashboard() }
        )
        "report_slow_sellers" -> ProductRankingReportScreen(
            viewModel = viewModel,
            isBestSellers = false,
            currentLanguage = currentLanguage,
            onBack = { viewModel.navigateToDashboard() }
        )
        "report_sales_by_category" -> SalesByCategoryReportScreen(
            viewModel = viewModel,
            currentLanguage = currentLanguage,
            onBack = { viewModel.navigateToDashboard() }
        )
        "report_low_stock" -> LowStockReportScreen(
            viewModel = viewModel,
            currentLanguage = currentLanguage,
            onBack = { viewModel.navigateToDashboard() }
        )
        "report_expenses" -> ExpensesReportScreen(
            viewModel = viewModel,
            currentLanguage = currentLanguage,
            onBack = { viewModel.navigateToDashboard() }
        )
        "report_monthly" -> MonthlyReportScreen(
            viewModel = viewModel,
            currentLanguage = currentLanguage,
            onBack = { viewModel.navigateToDashboard() }
        )
        "suppliers", "supplier_detail" -> SuppliersScreen(
            viewModel = viewModel,
            currentLanguage = currentLanguage,
            onBackToDashboard = { viewModel.navigateToDashboard() },
            modifier = modifier
        )
        "report_data_health" -> DataHealthScreen(
            viewModel = viewModel,
            currentLanguage = currentLanguage,
            onBack = { viewModel.navigateToDashboard() }
        )
        else -> {
            // Main Business Tab: matches layout and visual style of attached reference image
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(DarkSlateBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .testTag("screen_business"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Screen Title
                Text(
                    text = if (isBangla) "ব্যবসায়িক বিবরণ (Business Overview)" else "Business Overview",
                    style = AppTypographyScale.screenTitle.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )

                // Period Selector (Today, 7 days, Month, Year, Custom)
                PeriodSelectorPills(
                    selectedPeriod = period,
                    onPeriodSelected = { viewModel.selectPeriod(it) },
                    currentLanguage = currentLanguage,
                    modifier = Modifier.testTag("business_period_selector")
                )

                // ------------------------------------------------------------------
                // 1. OVERVIEW ROW (Total Sales, Profit, Orders) - Tapping opens Overview
                // ------------------------------------------------------------------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("business_overview_cards_row"),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isBangla) "মোট বিক্রি" else "Total Sales",
                        value = formatTaka(metrics.totalSales, currentLanguage),
                        valueColor = TextPrimary,
                        modifier = Modifier.weight(1.3f),
                        onClick = { viewModel.navigateTo("report_overview") }
                    )
                    StatCard(
                        title = if (isBangla) "লাভ" else "Profit",
                        value = formatTaka(metrics.profit, currentLanguage),
                        valueColor = VividEmeraldAccent,
                        modifier = Modifier.weight(1.2f),
                        onClick = { viewModel.navigateTo("report_overview") }
                    )
                    StatCard(
                        title = if (isBangla) "অর্ডার" else "Orders",
                        value = if (isBangla) toBengaliNumerals(metrics.orderCount.toString()) else metrics.orderCount.toString(),
                        valueColor = TextPrimary,
                        modifier = Modifier.weight(0.9f),
                        onClick = { viewModel.navigateTo("report_overview") }
                    )
                }

                // ------------------------------------------------------------------
                // 2. MONEY NOW CARD - Tapping opens Money Now report
                // ------------------------------------------------------------------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo("report_money_now") }
                        .testTag("card_money_now"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    border = BorderStroke(1.dp, DarkSlateCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isBangla) "এখনকার নগদ স্থিতি (Money Now)" else "Money Now",
                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "দোকানের মোট স্টক মূল্য" else "Stock Value",
                                style = AppTypographyScale.normalText,
                                color = TextMuted
                            )
                            Text(
                                text = formatTaka(moneyNow.stockValue, currentLanguage),
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "গ্রাহকের নিকট বাকি পাওনা" else "Baki owed to me",
                                style = AppTypographyScale.normalText,
                                color = TextMuted
                            )
                            Text(
                                text = formatTaka(moneyNow.bakiOwedToMe, currentLanguage),
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFF59E0B)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "মহাজনকে মোট দেনা" else "I owe suppliers",
                                style = AppTypographyScale.normalText,
                                color = TextMuted
                            )
                            Text(
                                text = formatTaka(moneyNow.iOweSuppliers, currentLanguage),
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                color = LowStockRed
                            )
                        }
                    }
                }

                // ------------------------------------------------------------------
                // 3. SALES CHART CARD - Tapping opens Sales Chart Report
                // ------------------------------------------------------------------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo("report_sales_chart") }
                        .testTag("card_sales_chart"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    border = BorderStroke(1.dp, DarkSlateCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "বিক্রয় চার্ট (Sales Chart)" else "Sales Chart",
                                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Bar Chart matching 4 weeks in reference image
                        SalesBarChartCanvas(
                            bars = salesChartBars,
                            currentLanguage = currentLanguage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        )
                    }
                }

                // ------------------------------------------------------------------
                // 4. BEST SELLERS & SALES BY CATEGORY ROW (2 Cards side by side)
                // ------------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Best Sellers Card (left)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("report_best_sellers") }
                            .testTag("card_best_sellers"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isBangla) "সর্বাধিক বিক্রীত" else "Best Sellers",
                                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // 3 product item boxes matching image
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val top3 = bestSellers.take(3)
                                top3.forEach { item ->
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkSlateSurfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        HardwareThumbnailIconSmall(photoKey = item.product.photoKey, category = item.product.category)
                                    }
                                }
                                if (top3.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkSlateSurfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Inventory, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Sales by Category Card (right)
                    val palette = listOf(
                        Color(0xFF22C55E), // Tools (Emerald)
                        Color(0xFF6EE7B7), // Paint (Light Mint)
                        Color(0xFF14B8A6), // Pipes (Teal)
                        Color(0xFFF97316)  // Electrical (Orange)
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("report_sales_by_category") }
                            .testTag("card_sales_by_category"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isBangla) "ক্যাটাগরি ভিত্তিক" else "Sales by Category",
                                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            // Donut chart matching image
                            CategoryDonutChart(
                                slices = salesByCategory,
                                palette = palette,
                                modifier = Modifier.size(80.dp)
                            )

                            // Legend tags matching image (Tools, Paint, Pipes, Electrical)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                salesByCategory.take(4).forEachIndexed { i, slice ->
                                    val color = palette[i % palette.size]
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(RoundedCornerShape(1.dp))
                                                .background(color)
                                        )
                                        Text(
                                            text = slice.categoryName.take(5),
                                            style = AppTypographyScale.hintText.copy(fontSize = 9.sp),
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ------------------------------------------------------------------
                // 5. BAKI & SUPPLIERS ROW (Matching image: Baki & Suppliers cards)
                // ------------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Baki Card (left) -> Opens Customer Baki Screen
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToBaki() }
                            .testTag("card_baki_overview"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isBangla) "বাকির খাতা" else "Baki",
                                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = if (isBangla) "গ্রাহকের মোট বাকি" else "Baki Owed to Me",
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                            Text(
                                text = formatTaka(moneyNow.bakiOwedToMe, currentLanguage),
                                style = AppTypographyScale.moneyNumber.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }

                    // Suppliers Card (right) -> Opens Suppliers Screen
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("suppliers") }
                            .testTag("card_suppliers_overview"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isBangla) "মহাজন ও সাপ্লায়ার" else "Suppliers",
                                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = if (isBangla) "দোকানের মোট দেনা" else "Suppliers owed",
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                            Text(
                                text = formatTaka(moneyNow.iOweSuppliers, currentLanguage),
                                style = AppTypographyScale.moneyNumber.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                                color = LowStockRed
                            )
                        }
                    }
                }

                // ------------------------------------------------------------------
                // 6. EXTRA REPORT SHORTCUT CARDS (Slow sellers, Low Stock, Expenses)
                // ------------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Expenses with Add Button
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("report_expenses") }
                            .testTag("card_shortcut_expenses"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBangla) "দোকান খরচ" else "Expenses", style = AppTypographyScale.hintText, color = TextMuted)
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(VividEmeraldContainer)
                                        .clickable { viewModel.openAddExpenseDialog() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Expense", tint = VividEmeraldAccent, modifier = Modifier.size(14.dp))
                                }
                            }
                            Text(
                                text = formatTaka(metrics.totalExpenses, currentLanguage),
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold, color = LowStockRed)
                            )
                        }
                    }

                    // Low Stock Alert shortcut
                    val lowStockList by viewModel.lowStockList.collectAsStateWithLifecycle()
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("report_low_stock") }
                            .testTag("card_shortcut_low_stock"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        border = BorderStroke(1.dp, if (lowStockList.isNotEmpty()) LowStockRed.copy(alpha = 0.5f) else DarkSlateCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBangla) "কম স্টক" else "Low Stock", style = AppTypographyScale.hintText, color = TextMuted)
                                Icon(Icons.Default.Warning, contentDescription = null, tint = LowStockRed, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = if (isBangla) "${toBengaliNumerals(lowStockList.size.toString())} টি পণ্য" else "${lowStockList.size} items",
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold, color = if (lowStockList.isNotEmpty()) LowStockRed else TextPrimary)
                            )
                        }
                    }

                    // Slow Sellers shortcut
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("report_slow_sellers") }
                            .testTag("card_shortcut_slow_sellers"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBangla) "কম বিক্রি" else "Slow Sellers", style = AppTypographyScale.hintText, color = TextMuted)
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = if (isBangla) "তালিকা দেখুন" else "View list",
                                style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Medium, color = VividEmeraldAccent)
                            )
                        }
                    }
                }

                // ------------------------------------------------------------------
                // 6.5. DATA HEALTH AUDIT CHECK CARD
                // ------------------------------------------------------------------
                val hasHealthIssues = dataHealthIssues.isNotEmpty()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo("report_data_health") }
                        .testTag("card_data_health_check"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    border = BorderStroke(
                        1.dp,
                        if (hasHealthIssues) WarmAmber.copy(alpha = 0.6f) else VividEmeraldAccent.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (hasHealthIssues) Color(0x33F59E0B) else VividEmeraldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (hasHealthIssues) Icons.Default.HealthAndSafety else Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = if (hasHealthIssues) WarmAmber else VividEmeraldAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = if (isBangla) "ডাটা স্বাস্থ্য পরীক্ষা (Data Health)" else "Data Health Check",
                                    style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isBangla)
                                        "স্টক, কাস্টমার বাকি ও মহাজন দেনা যাচাই"
                                    else
                                        "Audit stock levels, customer baki & supplier dues",
                                    style = AppTypographyScale.hintText,
                                    color = TextMuted
                                )
                            }
                        }

                        // Status pill badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (hasHealthIssues) LowStockRedContainer else VividEmeraldContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (hasHealthIssues) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = WarmAmber,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (isBangla)
                                            "${toBengaliNumerals(dataHealthIssues.size.toString())} টি অমিল"
                                        else
                                            "${dataHealthIssues.size} Mismatches",
                                        style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                                        color = WarmAmber
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = VividEmeraldAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (isBangla) "১০০% নির্ভুল" else "100% Healthy",
                                        style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                                        color = VividEmeraldAccent
                                    )
                                }
                            }
                        }
                    }
                }

                // ------------------------------------------------------------------
                // 7. DOWNLOAD MONTHLY REPORT (Matching pill button at bottom of image)
                // ------------------------------------------------------------------
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .border(1.dp, VividEmeraldAccent, RoundedCornerShape(26.dp))
                        .clickable {
                            isExportingPdf = true
                            viewModel.exportMonthlyReportPdf(context) { file ->
                                isExportingPdf = false
                                if (file != null) {
                                    Toast.makeText(
                                        context,
                                        if (isBangla) "মাসিক PDF রিপোর্ট প্রস্তুত ও ওপেন করা হয়েছে" else "Monthly PDF Report generated successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        if (isBangla) "PDF রিপোর্ট তৈরি ব্যর্থ হয়েছে" else "Failed to generate report PDF",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        .testTag("btn_download_monthly_report"),
                    color = DarkSlateBackground
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isExportingPdf) {
                            CircularProgressIndicator(
                                color = VividEmeraldAccent,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) "পিডিএফ রিপোর্ট তৈরি হচ্ছে..." else "Generating Monthly Report...",
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium, color = VividEmeraldAccent)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = VividEmeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) "মাসিক রিপোর্ট ডাউনলোড করুন (PDF)" else "Download Monthly Report",
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium, color = TextPrimary)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
            }
        }
    }
}
