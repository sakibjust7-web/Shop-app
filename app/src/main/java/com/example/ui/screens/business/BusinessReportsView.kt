package com.example.ui.screens.business

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.Customer
import com.example.data.local.entity.Expense
import com.example.data.local.entity.Product
import com.example.data.local.entity.SaleOrder
import com.example.ui.components.HardwareCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.formatTaka
import com.example.ui.localization.toBengaliNumerals
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportDetailsContainer(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onBack: () -> Unit,
    topAction: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSlateBackground)
            .testTag("report_details_container")
    ) {
        // Top Navigation Bar
        Surface(
            color = DarkSlateSurface,
            border = BorderStroke(1.dp, DarkSlateCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_report_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VividEmeraldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = VividEmeraldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = AppTypographyScale.screenTitle.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = AppTypographyScale.hintText,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (topAction != null) {
                    topAction()
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

// ==========================================
// 1. OVERVIEW REPORT DETAIL SCREEN
// ==========================================
@Composable
fun OverviewReportScreen(
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val period by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val metrics by viewModel.periodMetrics.collectAsStateWithLifecycle()
    val sales by viewModel.periodFilteredSales.collectAsStateWithLifecycle()
    val expenses by viewModel.periodFilteredExpenses.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA

    var showAllSales by remember { mutableStateOf(false) }

    ReportDetailsContainer(
        title = if (isBangla) "ব্যবসার সার্বিক বিবরণ (Overview)" else "Business Overview",
        subtitle = if (isBangla) "মোট বেচাকেনা, লাভ ও খরচ বিশ্লেষণ" else "Turnover, margins and operational costs",
        icon = Icons.Outlined.Analytics,
        onBack = onBack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                PeriodSelectorPills(
                    selectedPeriod = period,
                    onPeriodSelected = { viewModel.selectPeriod(it) },
                    currentLanguage = currentLanguage
                )
            }

            // Summary 3-pack card
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isBangla) "মোট বিক্রি" else "Total Sales",
                        value = formatTaka(metrics.totalSales, currentLanguage),
                        valueColor = TextPrimary,
                        modifier = Modifier.weight(1.3f)
                    )
                    StatCard(
                        title = if (isBangla) "মোট লাভ" else "Profit",
                        value = formatTaka(metrics.profit, currentLanguage),
                        valueColor = VividEmeraldAccent,
                        modifier = Modifier.weight(1.2f)
                    )
                    StatCard(
                        title = if (isBangla) "অর্ডার" else "Orders",
                        value = if (isBangla) toBengaliNumerals(metrics.orderCount.toString()) else metrics.orderCount.toString(),
                        valueColor = TextPrimary,
                        modifier = Modifier.weight(0.9f)
                    )
                }
            }

            // Margin breakdown card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    border = BorderStroke(1.dp, DarkSlateCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBangla) "লাভ ও খরচের হিসাব (Net Margin)" else "Margin & Cost Breakdown",
                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isBangla) "পণ্য বিক্রয় হতে লাভ" else "Gross Product Margin", style = AppTypographyScale.normalText, color = TextMuted)
                            Text(formatTaka(metrics.profit, currentLanguage), style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold), color = VividEmeraldAccent)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isBangla) "দোকানের মোট খরচ" else "Shop Expenses", style = AppTypographyScale.normalText, color = TextMuted)
                            Text("- ${formatTaka(metrics.totalExpenses, currentLanguage)}", style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold), color = LowStockRed)
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp),
                            color = DarkSlateCardBorder
                        ) {}

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "নিট ক্যাশ লাভ (Net Profit)" else "Net Business Margin",
                                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = formatTaka(metrics.netProfit, currentLanguage),
                                style = AppTypographyScale.moneyNumber.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (metrics.netProfit >= 0) VividEmeraldAccent else LowStockRed
                                )
                            )
                        }
                    }
                }
            }

            // Sales list (max 10 initially, with See All)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "সাম্প্রতিক বিক্রয় চালান (${sales.size})" else "Sales Invoices (${sales.size})",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    if (sales.size > 10) {
                        TextButton(
                            onClick = { showAllSales = !showAllSales },
                            modifier = Modifier.testTag("btn_see_all_sales")
                        ) {
                            Text(
                                text = if (showAllSales) (if (isBangla) "কম দেখুন" else "Show less") else (if (isBangla) "সব দেখুন" else "See all"),
                                style = AppTypographyScale.hintText.copy(color = VividEmeraldAccent, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            val displayedSales = if (showAllSales) sales else sales.take(10)
            if (displayedSales.isEmpty()) {
                item {
                    Text(
                        text = if (isBangla) "এই সময়ের মধ্যে কোনো বিক্রয় নেই" else "No sales recorded in this period",
                        style = AppTypographyScale.hintText,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(displayedSales, key = { it.id }) { sale ->
                    SimpleSaleOrderRow(sale = sale, currentLanguage = currentLanguage)
                }
            }
        }
    }
}

// ==========================================
// 2. MONEY NOW REPORT DETAIL SCREEN
// ==========================================
@Composable
fun MoneyNowReportScreen(
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val moneyNow by viewModel.moneyNowMetrics.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA

    ReportDetailsContainer(
        title = if (isBangla) "নগদ ও সম্পদ স্থিতি (Money Now)" else "Money Now",
        subtitle = if (isBangla) "ক্যাশ, বকেয়া বাকি, মহাজনি দেনা ও স্টকের মূল্য" else "Cash, receivables, supplier dues and inventory value",
        icon = Icons.Outlined.AccountBalanceWallet,
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Big 4 Cards
            MoneyNowCard(
                title = if (isBangla) "হাতে আনুমানিক নগদ টাকা (Cash in Hand)" else "Cash in Hand (Estimated)",
                amount = moneyNow.cashInHand,
                desc = if (isBangla) "আজকের বিক্রয় ও পূর্বের ক্যাশ ব্যালেন্স" else "Cash from paid sales minus shop expenses",
                color = VividEmeraldAccent,
                icon = Icons.Default.Payments,
                currentLanguage = currentLanguage
            )

            MoneyNowCard(
                title = if (isBangla) "মোট দোকানের স্টক পণ্যের মূল্য (Stock Value)" else "Stock Value",
                amount = moneyNow.stockValue,
                desc = if (isBangla) "ক্রয়মূল্যের ভিত্তিতে স্টকে থাকা সকল মালামাল" else "Current inventory valued at cost price",
                color = Color(0xFF60A5FA),
                icon = Icons.Default.Inventory,
                currentLanguage = currentLanguage
            )

            MoneyNowCard(
                title = if (isBangla) "গ্রাহকের নিকট বাকি পাওনা (Baki Owed to Me)" else "Baki Owed to Me",
                amount = moneyNow.bakiOwedToMe,
                desc = if (isBangla) "খরিদ্দারদের নিকট পাওনা বাকি টাকা" else "Total accounts receivable from credit customers",
                color = Color(0xFFF59E0B),
                icon = Icons.Default.ReceiptLong,
                currentLanguage = currentLanguage
            )

            MoneyNowCard(
                title = if (isBangla) "মহাজন ও সাপ্লায়ারের দেনা (I Owe Suppliers)" else "I Owe Suppliers",
                amount = moneyNow.iOweSuppliers,
                desc = if (isBangla) "পণ্য ক্রয়ের বকেয়া চালানের দেনা" else "Total unpaid debt to vendors and suppliers",
                color = LowStockRed,
                icon = Icons.Default.Business,
                currentLanguage = currentLanguage
            )

            // Net Shop Valuation
            val netWorth = (moneyNow.cashInHand + moneyNow.stockValue + moneyNow.bakiOwedToMe) - moneyNow.iOweSuppliers
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurfaceVariant),
                border = BorderStroke(1.dp, VividEmeraldAccent.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBangla) "দোকানের মোট নিট সম্পদ (Net Working Capital)" else "Net Working Capital & Assets",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                    Text(
                        text = formatTaka(netWorth, currentLanguage),
                        style = AppTypographyScale.moneyNumber.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VividEmeraldAccent)
                    )
                    Text(
                        text = if (isBangla) "ক্যাশ + স্টক + গ্রাহক বাকি - মহাজনের দেনা" else "Cash + Inventory + Customer Baki - Supplier Dues",
                        style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun MoneyNowCard(
    title: String,
    amount: Double,
    desc: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    currentLanguage: AppLanguage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Medium), color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatTaka(amount, currentLanguage),
                    style = AppTypographyScale.moneyNumber.copy(fontWeight = FontWeight.Bold, color = color)
                )
                Spacer(Modifier.height(2.dp))
                Text(text = desc, style = AppTypographyScale.hintText, color = TextMuted)
            }
        }
    }
}

// ==========================================
// 3. SALES CHART REPORT DETAIL SCREEN
// ==========================================
@Composable
fun SalesChartReportScreen(
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val period by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val bars by viewModel.salesChartBars.collectAsStateWithLifecycle()
    val metrics by viewModel.periodMetrics.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA

    ReportDetailsContainer(
        title = if (isBangla) "বিক্রয় চার্ট ও ট্রেন্ড (Sales Chart)" else "Sales Chart",
        subtitle = if (isBangla) "পিরিয়ড ভিত্তিক বিক্রয় ওঠানামা" else "Turnover trends and volume over time",
        icon = Icons.Default.BarChart,
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PeriodSelectorPills(
                selectedPeriod = period,
                onPeriodSelected = { viewModel.selectPeriod(it) },
                currentLanguage = currentLanguage
            )

            // Total Turnover stat
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(if (isBangla) "এই সময়ের মোট বিক্রয়" else "Period Sales Volume", style = AppTypographyScale.hintText, color = TextMuted)
                    Text(
                        formatTaka(metrics.totalSales, currentLanguage),
                        style = AppTypographyScale.moneyNumber.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VividEmeraldAccent)
                    )
                    Text(
                        if (isBangla) "সর্বমোট ${toBengaliNumerals(metrics.orderCount.toString())}টি বিক্রির চালান সম্পন্ন" else "${metrics.orderCount} completed customer sales",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                }
            }

            // Big readable Bar Chart
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = if (isBangla) "পর্যায়ক্রমিক বিক্রয় চিত্র" else "Sales Breakdown by Timeline",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    SalesBarChartCanvas(
                        bars = bars,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            // Breakdown table (4 rows)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBangla) "বিস্তারিত পরিসংখ্যান" else "Period Breakdown Table",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    bars.forEach { bar ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = bar.label, style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium), color = TextPrimary)
                                Text(
                                    text = if (isBangla) "${toBengaliNumerals(bar.orderCount.toString())} টি অর্ডার" else "${bar.orderCount} orders",
                                    style = AppTypographyScale.hintText,
                                    color = TextMuted
                                )
                            }
                            Text(
                                text = formatTaka(bar.amount, currentLanguage),
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                color = VividEmeraldAccent
                            )
                        }
                        Surface(modifier = Modifier.fillMaxWidth().height(1.dp), color = DarkSlateCardBorder) {}
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. BEST SELLERS & SLOW SELLERS DETAIL SCREENS
// ==========================================
@Composable
fun ProductRankingReportScreen(
    viewModel: BusinessViewModel,
    isBestSellers: Boolean,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val period by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val productStats by (if (isBestSellers) viewModel.bestSellers else viewModel.slowSellers).collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var showAll by remember { mutableStateOf(false) }

    val title = if (isBestSellers) {
        if (isBangla) "সর্বাধিক বিক্রীত পণ্য (Best Sellers)" else "Best Sellers"
    } else {
        if (isBangla) "কম বিক্রীত পণ্য (Slow Sellers)" else "Slow Sellers"
    }

    val subtitle = if (isBestSellers) {
        if (isBangla) "সবচেয়ে বেশি বিক্রি হওয়া শীর্ষ পণ্যসমূহ" else "Top items by turnover and unit quantity"
    } else {
        if (isBangla) "কম বিক্রি হওয়া অথবা জমে থাকা পণ্য" else "Items with stagnant movement or low sales"
    }

    ReportDetailsContainer(
        title = title,
        subtitle = subtitle,
        icon = if (isBestSellers) Icons.Default.ThumbUp else Icons.Default.ThumbDown,
        onBack = onBack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PeriodSelectorPills(
                    selectedPeriod = period,
                    onPeriodSelected = { viewModel.selectPeriod(it) },
                    currentLanguage = currentLanguage
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "পণ্য তালিকা (${productStats.size})" else "Products (${productStats.size})",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    if (productStats.size > 10) {
                        TextButton(
                            onClick = { showAll = !showAll },
                            modifier = Modifier.testTag("btn_toggle_see_all_products")
                        ) {
                            Text(
                                text = if (showAll) (if (isBangla) "কম দেখুন" else "Show less") else (if (isBangla) "সব দেখুন" else "See all"),
                                style = AppTypographyScale.hintText.copy(color = VividEmeraldAccent, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            val displayed = if (showAll) productStats else productStats.take(10)
            items(displayed, key = { it.product.id }) { stat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    border = BorderStroke(1.dp, DarkSlateCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSlateSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            HardwareThumbnailIconSmall(photoKey = stat.product.photoKey, category = stat.product.category)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stat.product.localizedName(isBangla),
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${stat.product.category} • ${if (isBangla) "স্টক:" else "Stock:"} ${stat.product.stockQuantity} ${stat.product.localizedUnit(isBangla)}",
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${if (isBangla) toBengaliNumerals(stat.soldQuantity.toString()) else stat.soldQuantity} ${stat.product.localizedUnit(isBangla)}",
                                style = AppTypographyScale.normalText.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBestSellers) VividEmeraldAccent else LowStockRed
                                )
                            )
                            Text(
                                text = formatTaka(stat.revenue, currentLanguage),
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. SALES BY CATEGORY REPORT DETAIL SCREEN
// ==========================================
@Composable
fun SalesByCategoryReportScreen(
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val period by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val slices by viewModel.salesByCategory.collectAsStateWithLifecycle()
    val metrics by viewModel.periodMetrics.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA

    val palette = listOf(
        Color(0xFF22C55E), // Emerald
        Color(0xFF60A5FA), // Blue
        Color(0xFFF59E0B), // Amber
        Color(0xFFF97316), // Orange
        Color(0xFFA855F7)  // Purple
    )

    ReportDetailsContainer(
        title = if (isBangla) "ক্যাটাগরি ভিত্তিক বিক্রয় (Sales by Category)" else "Sales by Category",
        subtitle = if (isBangla) "কোন ধরনের পণ্য কত শতাংশ বিক্রি হলো" else "Category distribution and revenue share",
        icon = Icons.Default.PieChart,
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PeriodSelectorPills(
                selectedPeriod = period,
                onPeriodSelected = { viewModel.selectPeriod(it) },
                currentLanguage = currentLanguage
            )

            // Donut Chart card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CategoryDonutChart(slices = slices, palette = palette)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (isBangla) "মোট বিক্রয়" else "Turnover", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(
                                text = formatTaka(metrics.totalSales, currentLanguage),
                                style = AppTypographyScale.moneyNumber.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                        }
                    }

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        slices.forEachIndexed { i, s ->
                            val color = palette[i % palette.size]
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(color)
                                )
                                Text(
                                    text = "${s.categoryName} (${(s.percentage * 100).toInt()}%)",
                                    style = AppTypographyScale.hintText,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Detail table
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isBangla) "ক্যাটাগরি অনুযায়ী টাকার বিবরণ" else "Category Revenue Details",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    slices.forEachIndexed { index, slice ->
                        val color = palette[index % palette.size]
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
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Column {
                                    Text(text = slice.categoryName, style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium), color = TextPrimary)
                                    Text(
                                        text = "${(slice.percentage * 100).toInt()}% • ${if (isBangla) "${toBengaliNumerals(slice.count.toString())}টি বিক্রি" else "${slice.count} orders"}",
                                        style = AppTypographyScale.hintText,
                                        color = TextMuted
                                    )
                                }
                            }
                            Text(
                                text = formatTaka(slice.amount, currentLanguage),
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                color = VividEmeraldAccent
                            )
                        }
                        if (index < slices.size - 1) {
                            Surface(modifier = Modifier.fillMaxWidth().height(1.dp), color = DarkSlateCardBorder) {}
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. LOW STOCK REPORT DETAIL SCREEN
// ==========================================
@Composable
fun LowStockReportScreen(
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val lowStockList by viewModel.lowStockList.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var showAll by remember { mutableStateOf(false) }

    ReportDetailsContainer(
        title = if (isBangla) "কম স্টকের মালামাল (Low Stock)" else "Low Stock Alerts",
        subtitle = if (isBangla) "যেসব পণ্যের পরিমাণ সতর্কতা সীমার নিচে" else "Inventory items below minimum reorder threshold",
        icon = Icons.Default.Warning,
        onBack = onBack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LowStockRedContainer),
                    border = BorderStroke(1.dp, LowStockRed.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = LowStockRed, modifier = Modifier.size(32.dp))
                        Column {
                            Text(
                                text = if (isBangla) "${toBengaliNumerals(lowStockList.size.toString())}টি পণ্যের স্টক কমে গেছে" else "${lowStockList.size} items urgently low in stock",
                                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = if (isBangla) "তাড়াতাড়ি সাপ্লায়ারের কাছে অর্ডার দিন" else "Contact suppliers to re-order stock",
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "সতর্কবার্তা তালিকা (${lowStockList.size})" else "Warning List (${lowStockList.size})",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    if (lowStockList.size > 10) {
                        TextButton(
                            onClick = { showAll = !showAll },
                            modifier = Modifier.testTag("btn_toggle_see_all_low_stock")
                        ) {
                            Text(
                                text = if (showAll) (if (isBangla) "কম দেখুন" else "Show less") else (if (isBangla) "সব দেখুন" else "See all"),
                                style = AppTypographyScale.hintText.copy(color = VividEmeraldAccent, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            val displayed = if (showAll) lowStockList else lowStockList.take(10)
            if (displayed.isEmpty()) {
                item {
                    Text(
                        text = if (isBangla) "আলহামদুলিল্লাহ, সকল পণ্যের পর্যাপ্ত স্টক রয়েছে।" else "All items are adequately stocked.",
                        style = AppTypographyScale.normalText,
                        color = VividEmeraldAccent,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(displayed, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        border = BorderStroke(1.dp, LowStockRed.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LowStockRedContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                HardwareThumbnailIconSmall(photoKey = product.photoKey, category = product.category)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.localizedName(isBangla),
                                    style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${product.category} • ${if (isBangla) "সতর্কতা সীমা:" else "Alert limit:"} ${product.lowStockThreshold} ${product.localizedUnit(isBangla)}",
                                    style = AppTypographyScale.hintText,
                                    color = TextMuted
                                )
                            }

                            Surface(
                                color = LowStockRedContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${if (isBangla) "বাকি:" else "Left:"} ${product.stockQuantity} ${product.localizedUnit(isBangla)}",
                                    style = AppTypographyScale.hintText.copy(color = LowStockRed, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. EXPENSES REPORT DETAIL SCREEN (WITH ADD-EXPENSE BUTTON)
// ==========================================
@Composable
fun ExpensesReportScreen(
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val period by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val expenses by viewModel.periodFilteredExpenses.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var showAll by remember { mutableStateOf(false) }

    val totalExp = expenses.sumOf { it.amount }

    ReportDetailsContainer(
        title = if (isBangla) "দোকানের খরচ (Expenses)" else "Shop Expenses",
        subtitle = if (isBangla) "দোকান পরিচালনা ও দৈনন্দিন ব্যয়ের খাতা" else "Store rent, electricity, maintenance and supplies",
        icon = Icons.Default.Receipt,
        onBack = onBack,
        topAction = {
            Button(
                onClick = { viewModel.openAddExpenseDialog() },
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_open_add_expense_dialog")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isBangla) "খরচ যোগ" else "Add", style = AppTypographyScale.buttonText.copy(fontSize = 13.sp))
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PeriodSelectorPills(
                    selectedPeriod = period,
                    onPeriodSelected = { viewModel.selectPeriod(it) },
                    currentLanguage = currentLanguage
                )
            }

            // Total expense stat
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    border = BorderStroke(1.dp, LowStockRed.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(if (isBangla) "এই পিরিয়ডের মোট খরচ" else "Total Expenses in Period", style = AppTypographyScale.hintText, color = TextMuted)
                        Text(
                            formatTaka(totalExp, currentLanguage),
                            style = AppTypographyScale.moneyNumber.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = LowStockRed)
                        )
                        Text(
                            if (isBangla) "মোট ${toBengaliNumerals(expenses.size.toString())}টি ব্যয়ের এন্ট্রি" else "${expenses.size} recorded expense entries",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "খরচের তালিকা (${expenses.size})" else "Expense Entries (${expenses.size})",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    if (expenses.size > 10) {
                        TextButton(
                            onClick = { showAll = !showAll },
                            modifier = Modifier.testTag("btn_toggle_see_all_expenses")
                        ) {
                            Text(
                                text = if (showAll) (if (isBangla) "কম দেখুন" else "Show less") else (if (isBangla) "সব দেখুন" else "See all"),
                                style = AppTypographyScale.hintText.copy(color = VividEmeraldAccent, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            val displayed = if (showAll) expenses else expenses.take(10)
            if (displayed.isEmpty()) {
                item {
                    Text(
                        text = if (isBangla) "এই সময়ের মধ্যে কোনো খরচ নথিভুক্ত নেই।" else "No expenses recorded in this period.",
                        style = AppTypographyScale.normalText,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(displayed, key = { it.id }) { expense ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(LowStockRedContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = LowStockRed, modifier = Modifier.size(18.dp))
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = expense.title,
                                    style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(expense.timestamp))
                                Text(
                                    text = "${expense.category} • $dateStr",
                                    style = AppTypographyScale.hintText,
                                    color = TextMuted
                                )
                                if (expense.note.isNotBlank()) {
                                    Text(
                                        text = expense.note,
                                        style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                                        color = TextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "- ${formatTaka(expense.amount, currentLanguage)}",
                                    style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold, color = LowStockRed)
                                )
                                IconButton(
                                    onClick = { viewModel.deleteExpense(expense.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. MONTHLY REPORT & PDF EXPORT SCREEN
// ==========================================
@Composable
fun MonthlyReportScreen(
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val metrics by viewModel.periodMetrics.collectAsStateWithLifecycle()
    val moneyNow by viewModel.moneyNowMetrics.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var isExporting by remember { mutableStateOf(false) }

    ReportDetailsContainer(
        title = if (isBangla) "মাসিক রিপোর্ট ও পিডিএফ (Monthly Report)" else "Monthly Report",
        subtitle = if (isBangla) "পূর্ণাঙ্গ মাসিক আর্থিক রিপোর্ট ও PDF ডাউনলোড" else "Comprehensive audit report and PDF document export",
        icon = Icons.Default.PictureAsPdf,
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // PDF Download Action Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = VividEmeraldContainer),
                border = BorderStroke(1.dp, VividEmeraldAccent)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }

                        Column {
                            Text(
                                text = if (isBangla) "অফিসিয়াল মাসিক PDF রিপোর্ট" else "Official Monthly PDF Report",
                                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            Text(
                                text = if (isBangla) "দোকানের সকল আয়, ব্যয় ও স্টকের হিসাব সংবলিত" else "Includes full income, expense, receivables & stock status",
                                style = AppTypographyScale.hintText.copy(color = Color.White.copy(alpha = 0.8f))
                            )
                        }
                    }

                    Button(
                        onClick = {
                            isExporting = true
                            viewModel.exportMonthlyReportPdf(context) { file ->
                                isExporting = false
                                if (file != null) {
                                    Toast.makeText(context, if (isBangla) "PDF রিপোর্ট তৈরি হয়েছে" else "PDF Report exported successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, if (isBangla) "PDF তৈরি ব্যর্থ হয়েছে" else "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_export_pdf_direct"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(color = VividEmeraldPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(if (isBangla) "পিডিএফ তৈরি হচ্ছে..." else "Generating PDF...", color = DarkSlateBackground, style = AppTypographyScale.buttonText)
                        } else {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = DarkSlateBackground, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(if (isBangla) "পিডিএফ রিপোর্ট ডাউনলোড করুন" else "Download Monthly Report (PDF)", color = DarkSlateBackground, style = AppTypographyScale.buttonText)
                        }
                    }
                }
            }

            // Summary of what will be in the report
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBangla) "মাসিক অডিট সারাংশ" else "Monthly Audit Summary",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isBangla) "মোট বিক্রয় টার্নওভার" else "Gross Sales", style = AppTypographyScale.normalText, color = TextMuted)
                        Text(formatTaka(metrics.totalSales, currentLanguage), style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isBangla) "মোট গ্রস লাভ" else "Gross Margin", style = AppTypographyScale.normalText, color = TextMuted)
                        Text(formatTaka(metrics.profit, currentLanguage), style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold), color = VividEmeraldAccent)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isBangla) "মোট দোকান খরচ" else "Shop Expenses", style = AppTypographyScale.normalText, color = TextMuted)
                        Text("- ${formatTaka(metrics.totalExpenses, currentLanguage)}", style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold), color = LowStockRed)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isBangla) "নিট প্রফিট মার্জিন" else "Net Margin", style = AppTypographyScale.normalText, color = TextMuted)
                        Text(formatTaka(metrics.netProfit, currentLanguage), style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold), color = if (metrics.netProfit >= 0) VividEmeraldAccent else LowStockRed)
                    }

                    Surface(modifier = Modifier.fillMaxWidth().height(1.dp), color = DarkSlateCardBorder) {}

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isBangla) "গ্রাহকের বাকি পাওনা" else "Customer Baki", style = AppTypographyScale.normalText, color = TextMuted)
                        Text(formatTaka(moneyNow.bakiOwedToMe, currentLanguage), style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold), color = Color(0xFFF59E0B))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isBangla) "মহাজনের দেনা" else "Supplier Dues", style = AppTypographyScale.normalText, color = TextMuted)
                        Text(formatTaka(moneyNow.iOweSuppliers, currentLanguage), style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold), color = LowStockRed)
                    }
                }
            }
        }
    }
}

// ==========================================
// COMMON HELPERS & WIDGETS
// ==========================================
@Composable
fun PeriodSelectorPills(
    selectedPeriod: BusinessTimePeriod,
    onPeriodSelected: (BusinessTimePeriod) -> Unit,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    Surface(
        color = DarkSlateSurface,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, DarkSlateCardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BusinessTimePeriod.values().forEach { period ->
                val isSelected = selectedPeriod == period
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) DarkSlateSurfaceVariant else Color.Transparent)
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) DarkSlateCardBorder else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onPeriodSelected(period) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) period.titleBn else period.titleEn,
                        style = AppTypographyScale.hintText.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TextPrimary else TextMuted
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, style = AppTypographyScale.hintText, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                text = value,
                style = AppTypographyScale.moneyNumber.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SalesBarChartCanvas(
    bars: List<WeeklySalesBar>,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val maxVal = (bars.maxOfOrNull { it.amount } ?: 1.0).coerceAtLeast(100.0)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEach { bar ->
                val fraction = (bar.amount / maxVal).toFloat().coerceIn(0.15f, 1f)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.48f)
                            .fillMaxHeight(fraction)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(VividEmeraldAccent)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bars.forEach { bar ->
                Text(
                    text = bar.label,
                    style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CategoryDonutChart(
    slices: List<CategorySalesSlice>,
    palette: List<Color>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(140.dp)) {
        val strokeWidth = 24.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        var currentStartAngle = -90f
        slices.forEachIndexed { i, slice ->
            val sweep = slice.percentage * 360f
            val color = palette[i % palette.size]
            drawArc(
                color = color,
                startAngle = currentStartAngle,
                sweepAngle = sweep - 2f, // Small gap between segments
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            currentStartAngle += sweep
        }
    }
}

@Composable
private fun SimpleSaleOrderRow(
    sale: SaleOrder,
    currentLanguage: AppLanguage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sale.customerName.ifBlank { "নগদ খরিদ্দার" },
                    style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium),
                    color = TextPrimary
                )
                val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(sale.timestamp))
                Text(
                    text = "#${sale.invoiceNumber} • $dateStr",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatTaka(sale.totalAmount, currentLanguage),
                    style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold, color = VividEmeraldAccent)
                )
                if (sale.dueAmount > 0) {
                    Text(
                        text = "বাকি: ${formatTaka(sale.dueAmount, currentLanguage)}",
                        style = AppTypographyScale.hintText.copy(color = LowStockRed, fontSize = 11.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun HardwareThumbnailIconSmall(
    photoKey: String,
    category: String,
    modifier: Modifier = Modifier
) {
    val icon = when (photoKey.lowercase()) {
        "hammer" -> Icons.Default.Build
        "pvc_pipe", "pipe" -> Icons.Default.Plumbing
        "paint" -> Icons.Default.FormatPaint
        "lock" -> Icons.Default.Lock
        "switch" -> Icons.Default.Power
        "nails" -> Icons.Default.Construction
        else -> when (category.lowercase()) {
            "paint & chemicals" -> Icons.Default.FormatPaint
            "pipes & fittings", "sanitary & bathroom" -> Icons.Default.Plumbing
            "electrical fittings" -> Icons.Default.Power
            "locks & door fittings" -> Icons.Default.Lock
            else -> Icons.Default.Construction
        }
    }
    Icon(icon, contentDescription = null, tint = VividEmeraldAccent, modifier = modifier.size(20.dp))
}

// ==========================================
// ADD EXPENSE DIALOG
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, category: String, note: String) -> Unit,
    currentLanguage: AppLanguage
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Shop Maintenance") }
    var note by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    val categories = listOf("Electricity", "Staff", "Transport", "Tea/Snacks", "Repair", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = DarkSlateSurface,
            border = BorderStroke(1.dp, DarkSlateCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (isBangla) "নতুন খরচ যুক্ত করুন" else "Record Shop Expense",
                    style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    label = { Text(if (isBangla) "খরচের বিবরণ *" else "Expense Title *") },
                    isError = titleError,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = VividEmeraldAccent,
                        unfocusedBorderColor = DarkSlateCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_expense_title")
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (isBangla) "খরচের পরিমাণ (টাকা) *" else "Amount (Taka) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = VividEmeraldAccent,
                        unfocusedBorderColor = DarkSlateCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_expense_amount")
                )

                Text(if (isBangla) "খরচের ধরন" else "Category", style = AppTypographyScale.hintText, color = TextMuted)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = AppTypographyScale.hintText) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VividEmeraldContainer,
                                selectedLabelColor = VividEmeraldOnContainer,
                                containerColor = DarkSlateSurfaceVariant,
                                labelColor = TextMuted
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (isBangla) "মন্তব্য / নোট (ঐচ্ছিক)" else "Note (Optional)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = VividEmeraldAccent,
                        unfocusedBorderColor = DarkSlateCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted, style = AppTypographyScale.buttonText)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = true
                                return@Button
                            }
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt <= 0) return@Button
                            onSave(title, amt, category, note)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_save_expense")
                    ) {
                        Text(if (isBangla) "সংরক্ষণ করুন" else "Save Expense", style = AppTypographyScale.buttonText)
                    }
                }
            }
        }
    }
}
