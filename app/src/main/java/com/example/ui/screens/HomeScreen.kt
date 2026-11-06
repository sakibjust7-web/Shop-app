package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.Product
import com.example.data.local.entity.SaleOrder
import com.example.ui.components.HardwareCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.formatTaka
import com.example.ui.localization.toBengaliNumerals
import com.example.ui.screens.home.HomeSummaryMetrics
import com.example.ui.screens.home.HomeViewModel
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

@Composable
fun HomeScreen(
    currentLanguage: AppLanguage,
    onNavigateToSell: () -> Unit = {},
    onNavigateToStock: () -> Unit = {},
    onNavigateToBaki: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val todayMetrics by viewModel.todayMetrics.collectAsStateWithLifecycle()
    val customerBaki by viewModel.customerBaki.collectAsStateWithLifecycle()
    val lowStockList by viewModel.lowStockProducts.collectAsStateWithLifecycle()
    val recentSalesList by viewModel.recentSales.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val todayDateFormatted = viewModel.getFormattedDate(currentLanguage)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSlateBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("screen_home"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Status & Backup Bar (matching reference image)
        SearchBarStatusPill(
            dateText = todayDateFormatted,
            isBangla = isBangla,
            onSettingsClick = onNavigateToSettings
        )

        // Large Easy Buttons: "New sale" & "Add product" (matching reference image style)
        LargeActionButtons(
            onNavigateToSell = onNavigateToSell,
            onNavigateToStock = onNavigateToStock,
            isBangla = isBangla
        )

        // "Today's Summary" Card with 4 columns and vertical dividers
        TodaysSummaryCard(
            metrics = todayMetrics,
            isBangla = isBangla
        )

        // Customer Baki & Suppliers row (side-by-side matching reference image)
        BakiAndSuppliersRow(
            customerBaki = customerBaki,
            currentLanguage = currentLanguage,
            isBangla = isBangla,
            onBakiClick = onNavigateToBaki
        )

        // "Low Stock" Card (matching reference image with item thumbnails & "Low" badge)
        LowStockSection(
            lowStockList = lowStockList,
            isBangla = isBangla
        )

        // "Recent Sales" Card (Last 5 sales on one line each)
        RecentSalesSection(
            salesList = recentSalesList,
            currentLanguage = currentLanguage,
            isBangla = isBangla
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

/**
 * Top pill showing Search icon with Today's Date and backup status
 */
@Composable
private fun SearchBarStatusPill(
    dateText: String,
    isBangla: Boolean,
    onSettingsClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, DarkSlateCardBorder, RoundedCornerShape(22.dp))
            .clickable { onSettingsClick() }
            .testTag("search_status_pill"),
        color = DarkSlateSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                val backupPrefix = if (isBangla) "আজকের তারিখ: " else "Today: "
                Text(
                    text = "$backupPrefix$dateText",
                    style = AppTypographyScale.hintText,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSlateSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = VividEmeraldAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isBangla) "সেটিংস ও ব্যাকআপ" else "Settings",
                        style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                        color = VividEmeraldAccent
                    )
                }
            }
        }
    }
}

/**
 * Large, easy buttons near the top:
 * "New sale" (Sell Product - large vivid emerald card)
 * and "Add product" (dark slate card with emerald accent)
 */
@Composable
private fun LargeActionButtons(
    onNavigateToSell: () -> Unit,
    onNavigateToStock: () -> Unit,
    isBangla: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // "New sale" (Sell Product) button in vibrant emerald green
        Card(
            modifier = Modifier
                .weight(1.3f)
                .height(76.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onNavigateToSell() }
                .testTag("btn_new_sale"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = VividEmeraldPrimary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Sell Product",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBangla) "পণ্য বিক্রি" else "Sell Product",
                    style = AppTypographyScale.buttonText.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // "Add product" button
        Card(
            modifier = Modifier
                .weight(1f)
                .height(76.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, DarkSlateCardBorder, RoundedCornerShape(16.dp))
                .clickable { onNavigateToStock() }
                .testTag("btn_add_product"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkSlateSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product",
                    tint = VividEmeraldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBangla) "পণ্য যোগ" else "Add Product",
                    style = AppTypographyScale.buttonText.copy(fontWeight = FontWeight.Medium),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * "Today's Summary" Card matching reference image:
 * Section heading "Today's Summary" with 4 columns divided by vertical dividers:
 * Total Sales | Profit | Orders | Items Sold
 */
@Composable
private fun TodaysSummaryCard(
    metrics: HomeSummaryMetrics,
    isBangla: Boolean
) {
    HardwareCard(modifier = Modifier.testTag("today_summary_card")) {
        Text(
            text = if (isBangla) "আজকের সারসংক্ষেপ" else "Today's Summary",
            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Total Sales
            val salesText = if (isBangla) {
                "৳" + toBengaliNumerals(metrics.totalSales.toLong().toString())
            } else {
                "৳" + metrics.totalSales.toLong().toString()
            }
            SummaryTileColumn(
                label = if (isBangla) "মোট বিক্রি" else "Total Sales",
                value = salesText,
                modifier = Modifier.weight(1f)
            )

            VerticalLineDivider()

            // 2. Profit
            val profitText = if (isBangla) {
                "৳" + toBengaliNumerals(metrics.totalProfit.toLong().toString())
            } else {
                "৳" + metrics.totalProfit.toLong().toString()
            }
            SummaryTileColumn(
                label = if (isBangla) "লাভ" else "Profit",
                value = profitText,
                modifier = Modifier.weight(1f)
            )

            VerticalLineDivider()

            // 3. Orders
            val ordersText = if (isBangla) {
                toBengaliNumerals(metrics.ordersCount.toString())
            } else {
                metrics.ordersCount.toString()
            }
            SummaryTileColumn(
                label = if (isBangla) "অর্ডার" else "Orders",
                value = ordersText,
                modifier = Modifier.weight(0.9f)
            )

            VerticalLineDivider()

            // 4. Items Sold
            val itemsText = if (isBangla) {
                toBengaliNumerals(metrics.itemsSoldCount.toString())
            } else {
                metrics.itemsSoldCount.toString()
            }
            SummaryTileColumn(
                label = if (isBangla) "বিক্রি সংখ্যা" else "Items Sold",
                value = itemsText,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun VerticalLineDivider() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(DarkSlateCardBorder)
    )
}

@Composable
private fun SummaryTileColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = AppTypographyScale.hintText,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = AppTypographyScale.moneyNumber.copy(
                fontSize = if (value.length > 6) 16.sp else 20.sp,
                lineHeight = if (value.length > 6) 22.sp else 28.sp,
                fontWeight = FontWeight.Bold
            ),
            color = VividEmeraldAccent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Customer Baki & Suppliers Row (Side-by-side matching the reference image)
 */
@Composable
private fun BakiAndSuppliersRow(
    customerBaki: Double,
    currentLanguage: AppLanguage,
    isBangla: Boolean,
    onBakiClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left: "Baki" (owed to me)
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, DarkSlateCardBorder, RoundedCornerShape(16.dp))
                .clickable { onBakiClick() }
                .testTag("customer_baki_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkSlateSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Baki Icon",
                    tint = TextMuted,
                    modifier = Modifier.size(28.dp)
                )

                Column {
                    Text(
                        text = if (isBangla) "বাকি" else "Baki",
                        style = AppTypographyScale.hintText,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val bakiFormatted = if (isBangla) {
                        "৳" + toBengaliNumerals(customerBaki.toLong().toString())
                    } else {
                        "৳" + customerBaki.toLong().toString()
                    }
                    Text(
                        text = bakiFormatted,
                        style = AppTypographyScale.moneyNumber.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = VividEmeraldAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isBangla) "পাওনা টাকা" else "owed to me",
                        style = AppTypographyScale.hintText,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Right: "Suppliers" (I owe)
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, DarkSlateCardBorder, RoundedCornerShape(16.dp))
                .testTag("suppliers_due_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkSlateSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = "Suppliers Icon",
                    tint = TextMuted,
                    modifier = Modifier.size(28.dp)
                )

                Column {
                    Text(
                        text = if (isBangla) "মহাজন/সাপ্লায়ার" else "Suppliers",
                        style = AppTypographyScale.hintText,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val supplierDue = 5800L
                    val supplierFormatted = if (isBangla) {
                        "৳" + toBengaliNumerals(supplierDue.toString())
                    } else {
                        "৳$supplierDue"
                    }
                    Text(
                        text = supplierFormatted,
                        style = AppTypographyScale.moneyNumber.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = VividEmeraldAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isBangla) "দেনা বাকি" else "I owe",
                        style = AppTypographyScale.hintText,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * "Low Stock" Section matching reference image:
 * Thumbnail on white rounded square, Product Name, Qty left, and Red "Low" pill badge.
 */
@Composable
private fun LowStockSection(
    lowStockList: List<Product>,
    isBangla: Boolean
) {
    HardwareCard(modifier = Modifier.testTag("low_stock_card")) {
        Text(
            text = if (isBangla) "কম স্টক সতর্কতা" else "Low Stock",
            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        if (lowStockList.isEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = VividEmeraldAccent,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isBangla) "সব পণ্যের পর্যাপ্ত স্টক রয়েছে।" else "All products are well stocked.",
                    style = AppTypographyScale.normalText,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                lowStockList.take(5).forEach { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: White rounded square with hardware tool/material icon
                        Surface(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(product.category),
                                    contentDescription = product.nameEn,
                                    tint = TextDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Middle: Product Name & Quantity
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.localizedName(isBangla),
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.SemiBold),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val qtyText = if (isBangla) {
                                "পরিমাণ: ${toBengaliNumerals(product.stockQuantity.toString())} ${product.unitBn}"
                            } else {
                                "Qty: ${product.stockQuantity} ${product.unitEn}"
                            }
                            Text(
                                text = qtyText,
                                style = AppTypographyScale.hintText,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Right: Red "Low" pill badge matching reference image
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LowStockRed
                        ) {
                            Text(
                                text = if (isBangla) "কম" else "Low",
                                style = AppTypographyScale.hintText.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * "Recent Sales" Section: Last 5 sales on one line each.
 */
@Composable
private fun RecentSalesSection(
    salesList: List<SaleOrder>,
    currentLanguage: AppLanguage,
    isBangla: Boolean
) {
    HardwareCard(modifier = Modifier.testTag("recent_sales_card")) {
        Text(
            text = if (isBangla) "সর্বশেষ বিক্রি" else "Recent Sales",
            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        if (salesList.isEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = VividEmeraldAccent,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isBangla) "আজকে এখনও কোনো বিক্রি রেকর্ড করা হয়নি।" else "No sales recorded yet today.",
                    style = AppTypographyScale.normalText,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                salesList.take(5).forEach { sale ->
                    val timeStr = SimpleDateFormat("hh:mm a", Locale.US).format(Date(sale.timestamp))
                    val timeFormatted = if (isBangla) toBengaliNumerals(timeStr) else timeStr

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: White rounded square icon
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "Sale Receipt",
                                    tint = TextDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Middle: Customer name & items
                        Column(modifier = Modifier.weight(1f)) {
                            val itemsWord = if (isBangla) "টি পণ্য" else "items"
                            val itemsCountStr = if (isBangla) toBengaliNumerals(sale.totalItemsCount.toString()) else sale.totalItemsCount.toString()
                            Text(
                                text = "${sale.customerName} ($itemsCountStr $itemsWord)",
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = timeFormatted,
                                style = AppTypographyScale.hintText,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Right: Amount in ৳ & Baki badge if any
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatTaka(sale.totalAmount, currentLanguage),
                                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                color = VividEmeraldAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (sale.dueAmount > 0) {
                                val bakiStr = if (isBangla) {
                                    "বাকি: " + formatTaka(sale.dueAmount, currentLanguage)
                                } else {
                                    "Due: " + formatTaka(sale.dueAmount, currentLanguage)
                                }
                                Text(
                                    text = bakiStr,
                                    style = AppTypographyScale.hintText,
                                    color = LowStockRed,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper to select an appropriate hardware icon based on category
 */
private fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase(Locale.ROOT)) {
        "paint" -> Icons.Default.FormatPaint
        "fasteners" -> Icons.Default.Construction
        "plumbing" -> Icons.Default.Plumbing
        "electrical" -> Icons.Default.Lightbulb
        else -> Icons.Default.Build
    }
}
