package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.formatTaka
import com.example.ui.localization.toBengaliNumerals
import com.example.ui.screens.history.DayGroupedHistory
import com.example.ui.screens.history.HistoryItem
import com.example.ui.screens.history.HistoryPeriodSummary
import com.example.ui.screens.history.HistoryViewModel
import com.example.ui.screens.history.TimePeriodFilter
import com.example.ui.screens.history.TransactionTypeFilter
import com.example.ui.theme.AppTypographyScale
import com.example.ui.theme.DarkSlateBackground
import com.example.ui.theme.DarkSlateCardBorder
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceVariant
import com.example.ui.theme.LowStockRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VividEmeraldAccent
import com.example.ui.theme.VividEmeraldPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel()
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val timeFilter by viewModel.timeFilter.collectAsStateWithLifecycle()
    val typeFilter by viewModel.typeFilter.collectAsStateWithLifecycle()
    val isDetailedView by viewModel.isDetailedView.collectAsStateWithLifecycle()
    val groupedHistory by viewModel.filteredGroupedHistory.collectAsStateWithLifecycle()
    val periodSummary by viewModel.periodSummary.collectAsStateWithLifecycle()
    val showDatePickerDialog by viewModel.showDatePickerDialog.collectAsStateWithLifecycle()
    val selectedItemForDetail by viewModel.selectedItemForDetail.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSlateBackground)
            .testTag("screen_history")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Controls: Date Filter Chips & Detailed Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time Filter Horizontal Chips matching reference image
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeFilterChip(
                        label = if (isBangla) "আজ" else "Today",
                        isSelected = timeFilter == TimePeriodFilter.TODAY,
                        onClick = { viewModel.setTimeFilter(TimePeriodFilter.TODAY) },
                        testTag = "filter_today"
                    )
                    TimeFilterChip(
                        label = if (isBangla) "চলতি সপ্তাহ" else "This Week",
                        isSelected = timeFilter == TimePeriodFilter.THIS_WEEK,
                        onClick = { viewModel.setTimeFilter(TimePeriodFilter.THIS_WEEK) },
                        testTag = "filter_this_week"
                    )
                    TimeFilterChip(
                        label = if (isBangla) "চলতি মাস" else "This Month",
                        isSelected = timeFilter == TimePeriodFilter.THIS_MONTH,
                        onClick = { viewModel.setTimeFilter(TimePeriodFilter.THIS_MONTH) },
                        testTag = "filter_this_month"
                    )
                    TimeFilterChip(
                        label = if (isBangla) "সব" else "All",
                        isSelected = timeFilter == TimePeriodFilter.ALL,
                        onClick = { viewModel.setTimeFilter(TimePeriodFilter.ALL) },
                        testTag = "filter_all"
                    )
                    TimeFilterChip(
                        label = if (isBangla) "কাস্টম" else "Custom",
                        isSelected = timeFilter == TimePeriodFilter.CUSTOM,
                        onClick = { viewModel.openDatePickerDialog() },
                        testTag = "filter_custom"
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Simple / Detailed Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.toggleDetailedView() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("toggle_detail_mode")
                ) {
                    Text(
                        text = if (isDetailedView) (if (isBangla) "বিস্তারিত" else "Detailed") else (if (isBangla) "সহজ" else "Simple"),
                        style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Medium),
                        color = if (isDetailedView) VividEmeraldAccent else TextMuted
                    )
                    Switch(
                        checked = isDetailedView,
                        onCheckedChange = { viewModel.toggleDetailedView() },
                        modifier = Modifier
                            .size(width = 38.dp, height = 24.dp)
                            .testTag("detailed_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = VividEmeraldPrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkSlateSurfaceVariant
                        )
                    )
                }
            }

            // Secondary Filter Row: All, Sales, Purchases
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypeFilterPill(
                    label = if (isBangla) "সব লেনদেন" else "All",
                    isSelected = typeFilter == TransactionTypeFilter.ALL,
                    onClick = { viewModel.setTypeFilter(TransactionTypeFilter.ALL) },
                    modifier = Modifier.weight(1f),
                    testTag = "type_filter_all"
                )
                TypeFilterPill(
                    label = if (isBangla) "বিক্রি" else "Sales",
                    isSelected = typeFilter == TransactionTypeFilter.SALES,
                    onClick = { viewModel.setTypeFilter(TransactionTypeFilter.SALES) },
                    modifier = Modifier.weight(1f),
                    testTag = "type_filter_sales"
                )
                TypeFilterPill(
                    label = if (isBangla) "ক্রয় (স্টক)" else "Purchases",
                    isSelected = typeFilter == TransactionTypeFilter.PURCHASES,
                    onClick = { viewModel.setTypeFilter(TransactionTypeFilter.PURCHASES) },
                    modifier = Modifier.weight(1f),
                    testTag = "type_filter_purchases"
                )
            }

            // Compact Total Summary Card matching reference image
            HistorySummaryCard(
                summary = periodSummary,
                currentLanguage = currentLanguage,
                isBangla = isBangla
            )

            // Grouped History Items List
            if (groupedHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if (isBangla) "নির্বাচিত সময়ে কোনো লেনদেন পাওয়া যায়নি" else "No transactions for selected period",
                            style = AppTypographyScale.normalText,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("history_list"),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groupedHistory.forEach { group ->
                        // Section Header (Day)
                        item(key = "header_${group.dateKey}") {
                            Text(
                                text = if (isBangla) group.dateLabelBn else group.dateLabelEn,
                                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                            )
                        }

                        // Transaction Rows for this day
                        items(group.items, key = { it.id }) { item ->
                            HistoryItemRow(
                                item = item,
                                isDetailed = isDetailedView,
                                currentLanguage = currentLanguage,
                                isBangla = isBangla,
                                onClick = { viewModel.selectItemForDetail(item) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedItemForDetail?.let { item ->
        HistoryDetailBottomSheet(
            item = item,
            currentLanguage = currentLanguage,
            isBangla = isBangla,
            onDismiss = { viewModel.selectItemForDetail(null) }
        )
    }

    // Custom Date Range Dialog
    if (showDatePickerDialog) {
        CustomDateRangeDialog(
            isBangla = isBangla,
            onRangeSelected = { start, end ->
                viewModel.setCustomDateRange(start, end)
            },
            onDismiss = { viewModel.closeDatePickerDialog() }
        )
    }
}

/**
 * Filter Chip matching reference pill style
 */
@Composable
private fun TimeFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) VividEmeraldPrimary else DarkSlateSurfaceVariant,
        border = if (isSelected) null else BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = AppTypographyScale.hintText.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) Color.White else TextPrimary
            )
        }
    }
}

/**
 * Secondary Type Filter Pill
 */
@Composable
private fun TypeFilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) DarkSlateSurfaceVariant else DarkSlateBackground,
        border = BorderStroke(1.dp, if (isSelected) VividEmeraldPrimary else DarkSlateCardBorder)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = AppTypographyScale.hintText.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) VividEmeraldAccent else TextMuted
            )
        }
    }
}

/**
 * Period Summary Card matching the reference image layout:
 * Bought: ৳24,000 | Sold: ৳31,500 | Profit: ৳7,500
 */
@Composable
private fun HistorySummaryCard(
    summary: HistoryPeriodSummary,
    currentLanguage: AppLanguage,
    isBangla: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_summary_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bought
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBangla) "কেনা (ক্রয়)" else "Bought",
                    style = AppTypographyScale.hintText,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatTaka(summary.bought, currentLanguage),
                    style = AppTypographyScale.normalText.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp)
                    .background(DarkSlateCardBorder)
            )

            // Sold
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBangla) "বিক্রি" else "Sold",
                    style = AppTypographyScale.hintText,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatTaka(summary.sold, currentLanguage),
                    style = AppTypographyScale.normalText.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp)
                    .background(DarkSlateCardBorder)
            )

            // Profit
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBangla) "মুনাফা" else "Profit",
                    style = AppTypographyScale.hintText,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatTaka(summary.profit, currentLanguage),
                    style = AppTypographyScale.normalText.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = VividEmeraldAccent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Individual Transaction Row matching reference image styling:
 * Left: photo box | Center: Name + [Sold]/[Bought]/[Baki] + Details | Right: Total ৳ + Profit
 */
@Composable
private fun HistoryItemRow(
    item: HistoryItem,
    isDetailed: Boolean,
    currentLanguage: AppLanguage,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
    val timeStr = timeFormat.format(Date(item.timestamp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag("history_item_${item.id}"),
        shape = RoundedCornerShape(10.dp),
        color = DarkSlateSurface,
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isDetailed) 10.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // White container for photo thumbnail (44dp detailed, 36dp simple)
            val thumbSize = if (isDetailed) 44.dp else 36.dp
            val iconSize = if (isDetailed) 26.dp else 20.dp
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                HistoryItemIcon(
                    photoKey = item.photoKey,
                    category = item.category,
                    modifier = Modifier.size(iconSize)
                )
            }

            // Middle Column: Title, Badge, Quantities & Time
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Title and Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.title,
                        style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Tag Badge: Sold / Bought / Baki
                    TransactionBadge(
                        type = item.type,
                        isCredit = item.isCredit,
                        isBangla = isBangla
                    )
                }

                // Subtitle Row: Quantity × Price, Time
                val qtyNum = if (isBangla) toBengaliNumerals(item.quantity.toString()) else item.quantity.toString()
                val priceNum = if (isBangla) toBengaliNumerals(item.unitPrice.toInt().toString()) else item.unitPrice.toInt().toString()

                val lineText = if (item.isCredit) {
                    if (isBangla) "বাকি $qtyNum × ৳$priceNum  $timeStr" else "Baki $qtyNum × ৳$priceNum  $timeStr"
                } else if (item.type == TransactionTypeFilter.PURCHASES) {
                    "$qtyNum × ৳$priceNum  $timeStr"
                } else {
                    if (isBangla) "বিক্রি $qtyNum × ৳$priceNum  $timeStr" else "Sold $qtyNum × ৳$priceNum  $timeStr"
                }

                Text(
                    text = lineText,
                    style = AppTypographyScale.hintText,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Detailed view extra: Party Name (Customer or Supplier)
                if (isDetailed && item.partyName.isNotBlank()) {
                    val partyPrefix = if (item.type == TransactionTypeFilter.SALES) {
                        if (isBangla) "ক্রেতা: " else "Customer: "
                    } else {
                        if (isBangla) "সরবরাহকারী: " else "Supplier: "
                    }
                    Text(
                        text = "$partyPrefix${item.partyName}",
                        style = AppTypographyScale.hintText,
                        color = VividEmeraldAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right Column: Total Amount & Profit (in detailed mode)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = formatTaka(item.totalAmount, currentLanguage),
                    style = AppTypographyScale.normalText.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isDetailed && item.type == TransactionTypeFilter.SALES) {
                    val profitAmount = formatTaka(item.profit, currentLanguage)
                    Text(
                        text = if (isBangla) "$profitAmount লাভ" else "$profitAmount profit",
                        style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Medium),
                        color = VividEmeraldAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Tag badge for transaction state: [Sold] in emerald, [Bought] in blue, [Baki] in amber
 */
@Composable
private fun TransactionBadge(
    type: TransactionTypeFilter,
    isCredit: Boolean,
    isBangla: Boolean
) {
    val (text, bgColor, textColor) = when {
        isCredit -> Triple(
            if (isBangla) "বাকি" else "Baki",
            Color(0xFFD97706),
            Color.White
        )
        type == TransactionTypeFilter.PURCHASES -> Triple(
            if (isBangla) "কেনা" else "Bought",
            Color(0xFF2563EB),
            Color.White
        )
        else -> Triple(
            if (isBangla) "বিক্রি" else "Sold",
            Color(0xFF059669),
            Color.White
        )
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            style = AppTypographyScale.hintText.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/**
 * Bottom Sheet displaying full transaction details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryDetailBottomSheet(
    item: HistoryItem,
    currentLanguage: AppLanguage,
    isBangla: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val timeFormat = SimpleDateFormat("EEEE, d MMMM yyyy • h:mm a", Locale.ENGLISH)
    val formattedDate = timeFormat.format(Date(item.timestamp))

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSlateSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkSlateSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.type == TransactionTypeFilter.SALES) Icons.Default.ShoppingCart else Icons.Default.Store,
                            contentDescription = null,
                            tint = VividEmeraldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (item.type == TransactionTypeFilter.SALES) {
                                if (isBangla) "বিক্রয় মেমো বিবরণ" else "Sale Memo Details"
                            } else {
                                if (isBangla) "ক্রয় চালান বিবরণ" else "Purchase Invoice Details"
                            },
                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = item.referenceNumber,
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted
                    )
                }
            }

            // Detail Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = DarkSlateSurfaceVariant,
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Date & Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "তারিখ ও সময়:" else "Date & Time:",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        Text(
                            text = formattedDate,
                            style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Medium),
                            color = TextPrimary
                        )
                    }

                    // Product Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "পণ্য:" else "Product:",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        Text(
                            text = item.title,
                            style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Quantity & Unit Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "পরিমাণ ও দর:" else "Quantity & Rate:",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        val qStr = if (isBangla) toBengaliNumerals(item.quantity.toString()) else item.quantity.toString()
                        Text(
                            text = "$qStr × ${formatTaka(item.unitPrice, currentLanguage)}",
                            style = AppTypographyScale.normalText,
                            color = TextPrimary
                        )
                    }

                    // Party (Customer / Supplier)
                    if (item.partyName.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (item.type == TransactionTypeFilter.SALES) {
                                    if (isBangla) "গ্রাহকের নাম:" else "Customer:"
                                } else {
                                    if (isBangla) "সরবরাহকারী:" else "Supplier:"
                                },
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                            Text(
                                text = item.partyName,
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium),
                                color = TextPrimary
                            )
                        }
                    }

                    // Payment Type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "লেনদেনের ধরন:" else "Payment Status:",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        TransactionBadge(
                            type = item.type,
                            isCredit = item.isCredit,
                            isBangla = isBangla
                        )
                    }

                    // Profit (if sale)
                    if (item.type == TransactionTypeFilter.SALES) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBangla) "অর্জিত মুনাফা:" else "Profit Earned:",
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                            Text(
                                text = formatTaka(item.profit, currentLanguage),
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                color = VividEmeraldAccent
                            )
                        }
                    }

                    // Total Amount
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "সর্বমোট টাকা:" else "Total Amount:",
                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = formatTaka(item.totalAmount, currentLanguage),
                            style = AppTypographyScale.moneyNumber.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            color = VividEmeraldAccent
                        )
                    }
                }
            }

            // Close button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(
                    text = if (isBangla) "ঠিক আছে" else "Close",
                    style = AppTypographyScale.buttonText,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Custom Date Range Dialog
 */
@Composable
private fun CustomDateRangeDialog(
    isBangla: Boolean,
    onRangeSelected: (startMs: Long, endMs: Long) -> Unit,
    onDismiss: () -> Unit
) {
    val now = System.currentTimeMillis()
    val dayMs = 86400000L

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "কাস্টম তারিখের পরিসীমা" else "Custom Date Range",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isBangla) "দ্রুত সময়কাল নির্বাচন করুন:" else "Select quick time frame:",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )

                // Presets
                listOf(
                    Pair(if (isBangla) "গত ৩ দিন" else "Last 3 Days", 3),
                    Pair(if (isBangla) "গত ৭ দিন" else "Last 7 Days", 7),
                    Pair(if (isBangla) "গত ১৪ দিন" else "Last 14 Days", 14),
                    Pair(if (isBangla) "গত ৩০ দিন" else "Last 30 Days", 30),
                    Pair(if (isBangla) "গত ৬০ দিন" else "Last 60 Days", 60)
                ).forEach { (label, days) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onRangeSelected(now - (days * dayMs), now)
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSlateSurfaceVariant,
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = AppTypographyScale.normalText,
                                color = TextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = VividEmeraldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.height(44.dp),
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Text(
                    text = if (isBangla) "বাতিল" else "Cancel",
                    style = AppTypographyScale.buttonText,
                    color = TextPrimary
                )
            }
        }
    )
}

/**
 * Hardware icon helper for history items
 */
@Composable
private fun HistoryItemIcon(
    photoKey: String,
    category: String,
    modifier: Modifier = Modifier
) {
    val icon = when (photoKey.lowercase()) {
        "hammer" -> Icons.Default.Build
        "pvc_pipe", "pipe" -> Icons.Default.Plumbing
        "paint" -> Icons.Default.FormatPaint
        "screwdriver" -> Icons.Default.Build
        "lock" -> Icons.Default.Lock
        "glasses" -> Icons.Default.Visibility
        "switch" -> Icons.Default.Power
        "drill" -> Icons.Default.Handyman
        "nails" -> Icons.Default.Construction
        "light" -> Icons.Default.Lightbulb
        else -> when (category.lowercase()) {
            "paint & chemicals" -> Icons.Default.FormatPaint
            "pipes & fittings", "sanitary & bathroom" -> Icons.Default.Plumbing
            "electrical fittings", "fans & appliances" -> Icons.Default.Power
            "lights & bulbs" -> Icons.Default.Lightbulb
            "locks & door fittings" -> Icons.Default.Lock
            "nuts bolts & screws" -> Icons.Default.Construction
            "safety equipment" -> Icons.Default.Visibility
            else -> Icons.Default.Build
        }
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = TextDark,
        modifier = modifier
    )
}
