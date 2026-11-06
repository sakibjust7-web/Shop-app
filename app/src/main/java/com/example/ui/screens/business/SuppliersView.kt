package com.example.ui.screens.business

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.entity.PurchaseRecord
import com.example.data.local.entity.Supplier
import com.example.data.local.entity.SupplierPayment
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
fun SuppliersScreen(
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentView by viewModel.currentView.collectAsStateWithLifecycle()
    val selectedSupplier by viewModel.selectedSupplier.collectAsStateWithLifecycle()
    val showAddDialog by viewModel.showAddSupplierDialog.collectAsStateWithLifecycle()
    val showRecordPaymentDialog by viewModel.showRecordPaymentDialog.collectAsStateWithLifecycle()

    BackHandler {
        if (currentView == "supplier_detail") {
            viewModel.navigateToSuppliers()
        } else {
            onBackToDashboard()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(DarkSlateBackground)) {
        if (currentView == "supplier_detail" && selectedSupplier != null) {
            SupplierDetailScreen(
                supplier = selectedSupplier!!,
                viewModel = viewModel,
                currentLanguage = currentLanguage,
                onBack = { viewModel.navigateToSuppliers() }
            )
        } else {
            SuppliersListScreen(
                viewModel = viewModel,
                currentLanguage = currentLanguage,
                onBack = onBackToDashboard,
                onSupplierClick = { supplier ->
                    viewModel.selectSupplier(supplier)
                },
                onAddSupplierClick = {
                    viewModel.openAddSupplierDialog()
                }
            )
        }

        if (showAddDialog) {
            AddSupplierDialog(
                currentLanguage = currentLanguage,
                onDismiss = { viewModel.closeAddSupplierDialog() },
                onConfirm = { name, company, phone, address, categories, note, owed ->
                    viewModel.saveSupplier(name, company, phone, address, categories, note, owed)
                }
            )
        }

        if (showRecordPaymentDialog && selectedSupplier != null) {
            RecordPaymentDialog(
                supplier = selectedSupplier!!,
                currentLanguage = currentLanguage,
                onDismiss = { viewModel.closeRecordPaymentDialog() },
                onConfirm = { amount, method, note ->
                    viewModel.recordPayment(amount, method, note)
                }
            )
        }
    }
}

@Composable
fun SuppliersListScreen(
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onSupplierClick: (Supplier) -> Unit,
    onAddSupplierClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val suppliers by viewModel.filteredSuppliers.collectAsStateWithLifecycle()
    val totalOwed by viewModel.totalAmountOwedToSuppliers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("screen_suppliers_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp).testTag("btn_back_to_business")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Column {
                    Text(
                        text = if (isBangla) "মহাজন ও সরবরাহকারী" else "Suppliers & Vendors",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                        color = TextPrimary
                    )
                    Text(
                        text = if (isBangla) "সর্বাধিক দেনা অনুযায়ী সাজানো" else "Biggest amount owed first",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                }
            }

            Button(
                onClick = onAddSupplierClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                modifier = Modifier.height(38.dp).testTag("btn_add_supplier")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (isBangla) "নতুন মহাজন" else "Add Supplier",
                    style = AppTypographyScale.buttonText
                )
            }
        }

        // Summary Card: Total Amount Owed to Suppliers
        Card(
            modifier = Modifier.fillMaxWidth().testTag("card_total_owed_suppliers"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
            border = BorderStroke(1.dp, if (totalOwed > 0) LowStockRed.copy(alpha = 0.5f) else DarkSlateCardBorder)
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (totalOwed > 0) LowStockRedContainer else VividEmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = if (totalOwed > 0) LowStockRed else VividEmeraldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isBangla) "দোকানের মোট মহাজনি দেনা" else "Total Shop Debt to Suppliers",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        Text(
                            text = formatTaka(totalOwed, currentLanguage),
                            style = AppTypographyScale.moneyNumber.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                            color = if (totalOwed > 0) LowStockRed else VividEmeraldAccent
                        )
                    }
                }

                Surface(
                    color = DarkSlateSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isBangla) "${toBengaliNumerals(suppliers.size.toString())} জন মহাজন" else "${suppliers.size} Suppliers",
                        style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Medium),
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier.fillMaxWidth().testTag("input_search_suppliers"),
            placeholder = {
                Text(
                    text = if (isBangla) "মহাজন, কোম্পানি বা ক্যাটাগরি খুঁজুন..." else "Search by name, company, category...",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSlateSurface,
                unfocusedContainerColor = DarkSlateSurface,
                focusedBorderColor = VividEmeraldPrimary,
                unfocusedBorderColor = DarkSlateCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        // Suppliers List
        if (suppliers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (isBangla) "কোন মহাজন পাওয়া যায়নি" else "No suppliers found",
                        style = AppTypographyScale.sectionHeading,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(suppliers, key = { it.id }) { supplier ->
                    SupplierItemCard(
                        supplier = supplier,
                        currentLanguage = currentLanguage,
                        onClick = { onSupplierClick(supplier) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SupplierItemCard(
    supplier: Supplier,
    currentLanguage: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val hasDebt = supplier.amountOwed > 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("supplier_item_${supplier.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, if (hasDebt) LowStockRed.copy(alpha = 0.35f) else DarkSlateCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = supplier.company.ifBlank { supplier.name },
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (supplier.company.isNotBlank() && supplier.name.isNotBlank()) {
                        Text(
                            text = supplier.name,
                            style = AppTypographyScale.hintText,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Amount Owed Badge
                Surface(
                    color = if (hasDebt) LowStockRedContainer else VividEmeraldContainer,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (hasDebt) LowStockRed.copy(alpha = 0.5f) else VividEmeraldAccent.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (hasDebt) {
                                if (isBangla) "দেনা: ${formatTaka(supplier.amountOwed, currentLanguage)}"
                                else "Owed: ${formatTaka(supplier.amountOwed, currentLanguage)}"
                            } else {
                                if (isBangla) "পরিশোধিত" else "Paid Up"
                            },
                            style = AppTypographyScale.hintText.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (hasDebt) LowStockRed else VividEmeraldAccent
                            )
                        )
                    }
                }
            }

            // Contact & Location info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (supplier.phone.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = VividEmeraldAccent, modifier = Modifier.size(14.dp))
                        Text(
                            text = if (isBangla) toBengaliNumerals(supplier.phone) else supplier.phone,
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                }

                if (supplier.address.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Text(
                            text = supplier.address,
                            style = AppTypographyScale.hintText,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Supplied Categories Chips
            if (supplier.suppliedCategories.isNotBlank()) {
                val categoriesList = supplier.suppliedCategories.split(",", "•", ";").map { it.trim() }.filter { it.isNotBlank() }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categoriesList.take(3).forEach { cat ->
                        Surface(
                            color = DarkSlateSurfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = cat,
                                style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (categoriesList.size > 3) {
                        Text(
                            text = "+${categoriesList.size - 3}",
                            style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                            color = TextMuted,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            // Note snippet if present
            if (supplier.note.isNotBlank()) {
                Text(
                    text = "📝 ${supplier.note}",
                    style = AppTypographyScale.hintText.copy(fontSize = 12.sp),
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Supplier Detail Screen:
 * - Shows how much the shop owes
 * - Button "Record payment to supplier"
 * - History of purchases and payments
 * - Summary of products bought from them (last buying price, total quantity)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SupplierDetailScreen(
    supplier: Supplier,
    viewModel: BusinessViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val purchases by viewModel.supplierPurchases.collectAsStateWithLifecycle()
    val payments by viewModel.supplierPayments.collectAsStateWithLifecycle()
    val productSummaries by viewModel.supplierProductSummaries.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Products Summary, 1: Purchase History, 2: Payment History
    var previewBillPhotoUri by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("screen_supplier_detail"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Back Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp).testTag("btn_back_to_suppliers")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = supplier.company.ifBlank { supplier.name },
                    style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isBangla) "মহাজন প্রোফাইল ও খাতা" else "Supplier Ledger & Profile",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )
            }
        }

        // Supplier Info & Big Debt Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("card_supplier_debt_banner"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
            border = BorderStroke(1.dp, if (supplier.amountOwed > 0) LowStockRed.copy(alpha = 0.5f) else VividEmeraldAccent.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name & Contact
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = supplier.name,
                            style = AppTypographyScale.sectionHeading.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        if (supplier.company.isNotBlank()) {
                            Text(
                                text = supplier.company,
                                style = AppTypographyScale.hintText,
                                color = VividEmeraldAccent
                            )
                        }
                    }

                    if (supplier.phone.isNotBlank()) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${supplier.phone}"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldContainer),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = VividEmeraldOnContainer, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "কল করুন" else "Call",
                                style = AppTypographyScale.hintText.copy(color = VividEmeraldOnContainer, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                // Address & Categories
                if (supplier.address.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        Text(
                            text = supplier.address,
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                }

                if (supplier.suppliedCategories.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Category, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        Text(
                            text = supplier.suppliedCategories,
                            style = AppTypographyScale.hintText,
                            color = TextPrimary
                        )
                    }
                }

                if (supplier.note.isNotBlank()) {
                    Text(
                        text = "📌 ${supplier.note}",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                }

                Spacer(Modifier.height(2.dp))

                // AMOUNT OWED BANNER
                Surface(
                    color = if (supplier.amountOwed > 0) LowStockRedContainer else VividEmeraldContainer,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (supplier.amountOwed > 0) LowStockRed.copy(alpha = 0.5f) else VividEmeraldAccent.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isBangla) "দোকানের মোট মহাজনি দেনা (Shop Owes):" else "Amount Shop Owes to this Supplier:",
                            style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Medium),
                            color = if (supplier.amountOwed > 0) LowStockRed else VividEmeraldAccent
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = formatTaka(supplier.amountOwed, currentLanguage),
                                style = AppTypographyScale.moneyNumber.copy(fontSize = 26.sp, fontWeight = FontWeight.Bold),
                                color = if (supplier.amountOwed > 0) LowStockRed else VividEmeraldAccent
                            )
                            if (supplier.amountOwed == 0.0) {
                                Surface(
                                    color = VividEmeraldPrimary,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "পরিশোধিত" else "Settled",
                                        style = AppTypographyScale.hintText.copy(color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // "RECORD PAYMENT TO SUPPLIER" BUTTON (Explicit user requirement)
                Button(
                    onClick = { viewModel.openRecordPaymentDialog() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_record_supplier_payment")
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "মহাজনকে মূল্য পরিশোধ করুন (Record Payment)" else "Record payment to supplier",
                        style = AppTypographyScale.buttonText.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // TAB ROW for Sections: 1. Products Summary, 2. Purchases History, 3. Payments History
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSlateSurface,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = VividEmeraldAccent
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = if (isBangla) "পণ্য সংক্ষেপ (${toBengaliNumerals(productSummaries.size.toString())})" else "Products (${productSummaries.size})",
                        style = AppTypographyScale.buttonText.copy(fontSize = 13.sp)
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = if (isBangla) "ক্রয় চালান (${toBengaliNumerals(purchases.size.toString())})" else "Purchases (${purchases.size})",
                        style = AppTypographyScale.buttonText.copy(fontSize = 13.sp)
                    )
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        text = if (isBangla) "পরিশোধ (${toBengaliNumerals(payments.size.toString())})" else "Payments (${payments.size})",
                        style = AppTypographyScale.buttonText.copy(fontSize = 13.sp)
                    )
                }
            )
        }

        // TAB 0: Summary of products bought from them (last buying price, total quantity)
        if (selectedTab == 0) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isBangla) "এই মহাজন থেকে ক্রয়কৃত পণ্যের সংক্ষিপ্ত বিবরণ" else "Summary of products bought from this supplier",
                    style = AppTypographyScale.sectionHeading.copy(fontSize = 15.sp),
                    color = TextPrimary
                )

                if (productSummaries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBangla) "এখনও কোন পণ্য ক্রয় রেকর্ড করা হয়নি।" else "No products bought from this supplier yet.",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                } else {
                    productSummaries.forEach { summary ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("product_summary_${summary.productId}"),
                            shape = RoundedCornerShape(10.dp),
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(DarkSlateSurfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Inventory2,
                                            contentDescription = null,
                                            tint = VividEmeraldAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = summary.productName,
                                            style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = if (isBangla) "মোট ক্রয়: ${toBengaliNumerals(summary.totalQuantity.toString())} টি"
                                            else "Total Quantity: ${summary.totalQuantity} pcs",
                                            style = AppTypographyScale.hintText,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (isBangla) "সর্বশেষ ক্রয়দর" else "Last Buying Price",
                                        style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                                        color = TextMuted
                                    )
                                    Text(
                                        text = formatTaka(summary.lastBuyingPrice, currentLanguage),
                                        style = AppTypographyScale.moneyNumber.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                        color = VividEmeraldAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 1: History of purchases (with bill photo thumbnails)
        if (selectedTab == 1) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isBangla) "ক্রয়কৃত চালানের পূর্ণ ইতিহাস" else "Purchase Invoices & History",
                    style = AppTypographyScale.sectionHeading.copy(fontSize = 15.sp),
                    color = TextPrimary
                )

                if (purchases.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBangla) "কোন ক্রয়ের রেকর্ড নেই" else "No purchase history found",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                } else {
                    val sdf = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
                    purchases.forEach { purchase ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("purchase_record_${purchase.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                            border = BorderStroke(1.dp, DarkSlateCardBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = purchase.productName,
                                        style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = formatTaka(purchase.totalCost, currentLanguage),
                                        style = AppTypographyScale.moneyNumber.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isBangla)
                                            "পরিমাণ: ${toBengaliNumerals(purchase.quantity.toString())} × ${formatTaka(purchase.unitBuyingPrice, currentLanguage)}"
                                        else
                                            "Qty: ${purchase.quantity} × ${formatTaka(purchase.unitBuyingPrice, currentLanguage)}",
                                        style = AppTypographyScale.hintText,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = sdf.format(Date(purchase.timestamp)),
                                        style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                                        color = TextMuted
                                    )
                                }

                                // Paid vs Due breakdown
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = if (isBangla) "পরিশোধ: ${formatTaka(purchase.paidAmount, currentLanguage)}"
                                            else "Paid: ${formatTaka(purchase.paidAmount, currentLanguage)}",
                                            style = AppTypographyScale.hintText.copy(color = VividEmeraldAccent, fontWeight = FontWeight.Medium)
                                        )
                                        if (purchase.dueAmount > 0) {
                                            Text(
                                                text = if (isBangla) "দেনা: ${formatTaka(purchase.dueAmount, currentLanguage)}"
                                                else "Due: ${formatTaka(purchase.dueAmount, currentLanguage)}",
                                                style = AppTypographyScale.hintText.copy(color = LowStockRed, fontWeight = FontWeight.Medium)
                                            )
                                        }
                                    }

                                    // Bill photo badge / thumbnail
                                    if (!purchase.billPhotoUri.isNullOrBlank()) {
                                        Surface(
                                            color = DarkSlateSurfaceVariant,
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.clickable { previewBillPhotoUri = purchase.billPhotoUri }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = VividEmeraldAccent, modifier = Modifier.size(14.dp))
                                                Text(
                                                    text = if (isBangla) "বিল ছবি" else "Bill Photo",
                                                    style = AppTypographyScale.hintText.copy(fontSize = 11.sp, color = VividEmeraldAccent)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (purchase.note.isNotBlank()) {
                                    Text(
                                        text = "নোট: ${purchase.note}",
                                        style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: History of payments
        if (selectedTab == 2) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isBangla) "মহাজনকে মূল্য পরিশোধের ইতিহাস" else "Payments Made to Supplier",
                    style = AppTypographyScale.sectionHeading.copy(fontSize = 15.sp),
                    color = TextPrimary
                )

                if (payments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBangla) "এখনও কোন পরিশোধের রেকর্ড নেই।" else "No payment records recorded yet.",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                } else {
                    val sdf = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
                    payments.forEach { payment ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("payment_record_${payment.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                            border = BorderStroke(1.dp, VividEmeraldAccent.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(VividEmeraldContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = VividEmeraldAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = formatTaka(payment.amount, currentLanguage),
                                            style = AppTypographyScale.moneyNumber.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                            color = VividEmeraldAccent
                                        )
                                        Text(
                                            text = "${payment.paymentMethod} • ${sdf.format(Date(payment.timestamp))}",
                                            style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                                            color = TextMuted
                                        )
                                        if (payment.note.isNotBlank()) {
                                            Text(
                                                text = payment.note,
                                                style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    color = VividEmeraldContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "পরিশোধ" else "Paid",
                                        style = AppTypographyScale.hintText.copy(fontSize = 11.sp, color = VividEmeraldOnContainer, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Bill Photo Preview Dialog
    previewBillPhotoUri?.let { uri ->
        Dialog(onDismissRequest = { previewBillPhotoUri = null }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "সংযুক্ত বিলের ছবি" else "Attached Bill Photo",
                            style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        IconButton(onClick = { previewBillPhotoUri = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                        }
                    }

                    AsyncImage(
                        model = uri,
                        contentDescription = "Bill Photo Preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

/**
 * Record Payment Dialog
 */
@Composable
fun RecordPaymentDialog(
    supplier: Supplier,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, paymentMethod: String, note: String) -> Unit
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var amountText by remember { mutableStateOf(if (supplier.amountOwed > 0) supplier.amountOwed.toLong().toString() else "5000") }
    var selectedMethod by remember { mutableStateOf("Cash") }
    var note by remember { mutableStateOf("") }

    val methods = listOf("Cash", "bKash", "Bank Transfer", "Cheque")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "মহাজনকে মূল্য পরিশোধ রেকর্ড" else "Record Payment to Supplier",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${supplier.company.ifBlank { supplier.name }} • ${if (isBangla) "বর্তমান দেনা:" else "Current Debt:"} ${formatTaka(supplier.amountOwed, currentLanguage)}",
                    style = AppTypographyScale.hintText,
                    color = if (supplier.amountOwed > 0) LowStockRed else VividEmeraldAccent
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (isBangla) "পরিশোধের পরিমাণ (৳)" else "Payment Amount (৳)", style = AppTypographyScale.hintText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_record_payment_amount"),
                    colors = dialogTextFieldColors()
                )

                Text(
                    text = if (isBangla) "পরিশোধ মাধ্যম:" else "Payment Method:",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    methods.forEach { method ->
                        val isSelected = selectedMethod == method
                        Surface(
                            color = if (isSelected) VividEmeraldPrimary else DarkSlateSurfaceVariant,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethod = method }
                        ) {
                            Text(
                                text = method,
                                style = AppTypographyScale.hintText.copy(
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(vertical = 6.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (isBangla) "নোট / ভাউচার / ট্রানজেকশন আইডি" else "Note / Voucher / TrxID", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_record_payment_note"),
                    colors = dialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount, selectedMethod, note)
                    }
                },
                modifier = Modifier.height(44.dp).testTag("btn_confirm_record_payment"),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(
                    text = if (isBangla) "পরিশোধ সম্পন্ন করুন" else "Confirm Payment",
                    style = AppTypographyScale.buttonText
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    text = if (isBangla) "বাতিল" else "Cancel",
                    style = AppTypographyScale.buttonText,
                    color = TextMuted
                )
            }
        }
    )
}

/**
 * Add Supplier Dialog
 */
@Composable
fun AddSupplierDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, company: String, phone: String, address: String, categories: String, note: String, amountOwed: Double) -> Unit
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var categories by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var owedText by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "নতুন মহাজন / সরবরাহকারী যুক্ত করুন" else "Add New Supplier",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text(if (isBangla) "কোম্পানি / প্রতিষ্ঠানের নাম *" else "Company / Enterprise *", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_supplier_company"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBangla) "মহাজন / প্রতিনিধির নাম *" else "Contact Person Name *", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_supplier_name"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isBangla) "মোবাইল নম্বর" else "Phone Number", style = AppTypographyScale.hintText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_supplier_phone"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(if (isBangla) "ঠিকানা" else "Address", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_supplier_address"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = categories,
                    onValueChange = { categories = it },
                    label = { Text(if (isBangla) "সরবরাহকৃত ক্যাটাগরি (কমা দিয়ে)" else "Supplied Categories (e.g. Tools, Pipes)", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_supplier_categories"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = owedText,
                    onValueChange = { owedText = it },
                    label = { Text(if (isBangla) "প্রারম্ভিক দেনা (যদি থাকে ৳)" else "Initial Amount Owed (৳)", style = AppTypographyScale.hintText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_supplier_owed"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (isBangla) "নোট / শর্তাবলী" else "Note / Terms", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_supplier_note"),
                    colors = dialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (company.isNotBlank() || name.isNotBlank()) {
                        val owed = owedText.toDoubleOrNull() ?: 0.0
                        onConfirm(name, company, phone, address, categories, note, owed)
                    }
                },
                modifier = Modifier.height(44.dp).testTag("btn_confirm_add_supplier"),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(
                    text = if (isBangla) "সংরক্ষণ করুন" else "Save Supplier",
                    style = AppTypographyScale.buttonText
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    text = if (isBangla) "বাতিল" else "Cancel",
                    style = AppTypographyScale.buttonText,
                    color = TextMuted
                )
            }
        }
    )
}

@Composable
private fun dialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = DarkSlateSurfaceVariant,
    unfocusedContainerColor = DarkSlateSurfaceVariant,
    focusedBorderColor = VividEmeraldPrimary,
    unfocusedBorderColor = DarkSlateCardBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)
