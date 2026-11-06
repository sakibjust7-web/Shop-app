package com.example.ui.screens.baki

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.Customer
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
import com.example.ui.theme.VividEmeraldPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun isCustomerOverdue(customer: Customer): Boolean {
    if (customer.totalBaki <= 0) return false
    val now = System.currentTimeMillis()
    val sevenDaysMs = 7L * 24 * 60 * 60 * 1000L

    // 1. If due date is set, check if due date has passed
    if (customer.dueDate != null && customer.dueDate > 0) {
        return customer.dueDate < now
    }

    // 2. With no due date, check if nothing was paid for 7 days
    if (customer.lastPaymentDate != null && customer.lastPaymentDate > 0) {
        return (now - customer.lastPaymentDate) > sevenDaysMs
    }

    // If never paid, check if last credit transaction was over 7 days ago
    return (now - customer.lastTransactionTime) > sevenDaysMs
}

@Composable
fun BakiScreen(
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BakiViewModel = viewModel()
) {
    val selectedCustomer by viewModel.selectedCustomer.collectAsStateWithLifecycle()
    val showAddCustomerDialog by viewModel.showAddCustomerDialog.collectAsStateWithLifecycle()
    val showRecordPaymentDialog by viewModel.showRecordPaymentDialog.collectAsStateWithLifecycle()
    val showAddNoteDialog by viewModel.showAddNoteDialog.collectAsStateWithLifecycle()
    val entryToEdit by viewModel.entryToEdit.collectAsStateWithLifecycle()
    val entryToDelete by viewModel.entryToDelete.collectAsStateWithLifecycle()

    BackHandler {
        if (selectedCustomer != null) {
            viewModel.selectCustomer(null)
        } else {
            onBack()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(DarkSlateBackground)) {
        if (selectedCustomer != null) {
            CustomerBakiDetailScreen(
                customer = selectedCustomer!!,
                viewModel = viewModel,
                currentLanguage = currentLanguage,
                onBack = { viewModel.selectCustomer(null) }
            )
        } else {
            CustomerBakiListScreen(
                viewModel = viewModel,
                currentLanguage = currentLanguage,
                onBack = onBack,
                onCustomerClick = { customer -> viewModel.selectCustomer(customer) },
                onAddCustomerClick = { viewModel.openAddCustomerDialog() }
            )
        }

        // Dialog: Add Customer
        if (showAddCustomerDialog) {
            AddCustomerDialog(
                currentLanguage = currentLanguage,
                onDismiss = { viewModel.closeAddCustomerDialog() },
                onConfirm = { name, phone, address, note, initialBaki ->
                    viewModel.addCustomer(name, phone, address, note, initialBaki)
                }
            )
        }

        // Dialog: Record Payment
        if (showRecordPaymentDialog && selectedCustomer != null) {
            RecordCustomerPaymentDialog(
                customer = selectedCustomer!!,
                currentLanguage = currentLanguage,
                onDismiss = { viewModel.closeRecordPaymentDialog() },
                onConfirm = { amount, note ->
                    viewModel.recordPayment(amount, note)
                }
            )
        }

        // Dialog: Add Note
        if (showAddNoteDialog && selectedCustomer != null) {
            AddCustomerNoteDialog(
                customer = selectedCustomer!!,
                currentLanguage = currentLanguage,
                onDismiss = { viewModel.closeAddNoteDialog() },
                onConfirm = { note ->
                    viewModel.addCustomerNote(note)
                }
            )
        }

        // Dialog: Edit Ledger Entry
        entryToEdit?.let { entry ->
            EditLedgerEntryDialog(
                entry = entry,
                currentLanguage = currentLanguage,
                onDismiss = { viewModel.closeEditEntry() },
                onConfirmPaymentEdit = { newAmount, newNote ->
                    viewModel.updatePaymentEntry(entry.id, newAmount, newNote)
                },
                onConfirmSaleEdit = { newDueAmount, newNote, newDueDate ->
                    if (entry is CustomerLedgerItem.Sale) {
                        viewModel.updateCreditSaleEntry(entry.saleOrder, newDueAmount, newNote, newDueDate)
                    }
                }
            )
        }

        // Dialog: Delete Ledger Entry confirmation
        entryToDelete?.let { entry ->
            DeleteLedgerEntryConfirmDialog(
                entry = entry,
                currentLanguage = currentLanguage,
                onDismiss = { viewModel.closeDeleteEntry() },
                onConfirm = {
                    when (entry) {
                        is CustomerLedgerItem.Payment -> viewModel.deletePaymentEntry(entry.id)
                        is CustomerLedgerItem.Sale -> viewModel.deleteCreditSaleEntry(entry.id)
                    }
                }
            )
        }
    }
}

@Composable
fun CustomerBakiListScreen(
    viewModel: BakiViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onCustomerClick: (Customer) -> Unit,
    onAddCustomerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.filteredCustomers.collectAsStateWithLifecycle()
    val totalBaki by viewModel.totalCustomerBaki.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("screen_customer_baki_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header
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
                    modifier = Modifier.size(40.dp).testTag("btn_back_from_baki")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Column {
                    Text(
                        text = if (isBangla) "বাকির খাতা (Customer Baki)" else "Customer Baki Ledger",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                        color = TextPrimary
                    )
                    Text(
                        text = if (isBangla) "সর্বাধিক বাকি অনুযায়ী সাজানো" else "Biggest balance owed first",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                }
            }

            // "+" Button adds a customer (Explicit requirement)
            Button(
                onClick = onAddCustomerClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                modifier = Modifier.height(38.dp).testTag("btn_add_baki_customer")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (isBangla) "নতুন গ্রাহক" else "Add Customer",
                    style = AppTypographyScale.buttonText
                )
            }
        }

        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("card_total_customer_baki"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
            border = BorderStroke(1.dp, if (totalBaki > 0) LowStockRed.copy(alpha = 0.5f) else DarkSlateCardBorder)
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
                            .background(if (totalBaki > 0) LowStockRedContainer else VividEmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = if (totalBaki > 0) LowStockRed else VividEmeraldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isBangla) "গ্রাহকের মোট বকেয়া বাকি" else "Total Customer Balance Owed",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        Text(
                            text = formatTaka(totalBaki, currentLanguage),
                            style = AppTypographyScale.moneyNumber.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                            color = if (totalBaki > 0) LowStockRed else VividEmeraldAccent
                        )
                    }
                }

                Surface(
                    color = DarkSlateSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isBangla) "${toBengaliNumerals(customers.size.toString())} জন খরিদ্দার" else "${customers.size} Customers",
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
            modifier = Modifier.fillMaxWidth().testTag("input_search_baki_customers"),
            placeholder = {
                Text(
                    text = if (isBangla) "গ্রাহকের নাম বা মোবাইল নম্বর দিয়ে খুঁজুন..." else "Search by customer name or phone...",
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

        // Customer List (Biggest balance first)
        if (customers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (isBangla) "কোন বাকির গ্রাহক পাওয়া যায়নি" else "No baki customers found",
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
                items(customers, key = { it.id }) { customer ->
                    CustomerBakiRow(
                        customer = customer,
                        currentLanguage = currentLanguage,
                        onClick = { onCustomerClick(customer) }
                    )
                }
            }
        }
    }
}

/**
 * Customer Row:
 * name, phone, balance owed, and red "Overdue" tag if due date passed or nothing paid for 7 days
 */
@Composable
fun CustomerBakiRow(
    customer: Customer,
    currentLanguage: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val overdue = isCustomerOverdue(customer)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("baki_customer_row_${customer.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, if (overdue) LowStockRed.copy(alpha = 0.5f) else DarkSlateCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = customer.name,
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // RED "OVERDUE" TAG (Explicit user requirement)
                    if (overdue) {
                        Surface(
                            color = LowStockRedContainer,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, LowStockRed)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = LowStockRed, modifier = Modifier.size(12.dp))
                                Text(
                                    text = if (isBangla) "সময় উত্তীর্ণ (Overdue)" else "Overdue",
                                    style = AppTypographyScale.hintText.copy(
                                        color = LowStockRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                if (customer.phone.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                        Text(
                            text = if (isBangla) toBengaliNumerals(customer.phone) else customer.phone,
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                }

                if (customer.note.isNotBlank()) {
                    Text(
                        text = "📝 ${customer.note}",
                        style = AppTypographyScale.hintText.copy(fontSize = 12.sp),
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Balance Owed (Right side)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isBangla) "বাকি পাওনা" else "Balance Owed",
                    style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                    color = TextMuted
                )
                Text(
                    text = formatTaka(customer.totalBaki, currentLanguage),
                    style = AppTypographyScale.moneyNumber.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (overdue) LowStockRed else if (customer.totalBaki > 0) Color(0xFFF59E0B) else VividEmeraldAccent
                )
            }
        }
    }
}

/**
 * Customer Baki Detail Screen:
 * - current balance
 * - button "Record payment" (amount and note)
 * - button "Add note"
 * - full list of credit sales and payments
 * - call button for phone number
 * - allow editing or deleting an entry; balances update correctly when an entry changes
 */
@Composable
fun CustomerBakiDetailScreen(
    customer: Customer,
    viewModel: BakiViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ledgerEntries by viewModel.customerLedger.collectAsStateWithLifecycle()
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val context = LocalContext.current
    val overdue = isCustomerOverdue(customer)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("screen_customer_baki_detail"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Back header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp).testTag("btn_back_to_baki_list")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.name,
                    style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isBangla) "গ্রাহকের বাকির খাতা ও লেজার" else "Customer Credit Ledger",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )
            }
        }

        // Current Balance & Action Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("card_customer_balance_banner"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
            border = BorderStroke(1.dp, if (overdue) LowStockRed.copy(alpha = 0.5f) else VividEmeraldAccent.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name & Call button (Explicit user requirement)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = customer.name,
                            style = AppTypographyScale.sectionHeading.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        if (customer.phone.isNotBlank()) {
                            Text(
                                text = if (isBangla) toBengaliNumerals(customer.phone) else customer.phone,
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                        }
                    }

                    if (customer.phone.isNotBlank()) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.phone}"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldContainer),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(38.dp).testTag("btn_call_customer")
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = VividEmeraldAccent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isBangla) "কল করুন" else "Call",
                                style = AppTypographyScale.buttonText.copy(color = VividEmeraldAccent, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                if (customer.address.isNotBlank()) {
                    Text(
                        text = "📍 ${customer.address}",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                }

                // Balance Banner
                Surface(
                    color = if (overdue) LowStockRedContainer else DarkSlateSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (overdue) LowStockRed.copy(alpha = 0.5f) else DarkSlateCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isBangla) "বর্তমান মোট বাকি (Current Balance):" else "Current Balance Owed:",
                                style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Medium),
                                color = if (overdue) LowStockRed else TextMuted
                            )
                            Text(
                                text = formatTaka(customer.totalBaki, currentLanguage),
                                style = AppTypographyScale.moneyNumber.copy(fontSize = 26.sp, fontWeight = FontWeight.Bold),
                                color = if (overdue) LowStockRed else if (customer.totalBaki > 0) Color(0xFFF59E0B) else VividEmeraldAccent
                            )
                        }

                        if (overdue) {
                            Surface(
                                color = LowStockRed,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isBangla) "বকেয়া মেয়াদোত্তীর্ণ" else "OVERDUE",
                                    style = AppTypographyScale.hintText.copy(color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Notes section
                if (customer.note.isNotBlank()) {
                    Surface(
                        color = DarkSlateSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (isBangla) "গ্রাহক নোট:" else "Customer Notes:",
                                style = AppTypographyScale.hintText.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = TextMuted
                            )
                            Text(
                                text = customer.note,
                                style = AppTypographyScale.normalText.copy(fontSize = 13.sp),
                                color = TextPrimary
                            )
                        }
                    }
                }

                // TWO BUTTONS (Explicit user requirements): "Record payment" & "Add note"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Button "Record payment"
                    Button(
                        onClick = { viewModel.openRecordPaymentDialog() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("btn_record_customer_payment")
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "জমা / পরিশোধ (Record payment)" else "Record payment",
                            style = AppTypographyScale.buttonText.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Button "Add note"
                    OutlinedButton(
                        onClick = { viewModel.openAddNoteDialog() },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, VividEmeraldAccent.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_add_customer_note")
                    ) {
                        Icon(Icons.Default.NoteAdd, contentDescription = null, tint = VividEmeraldAccent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isBangla) "নোট যুক্ত করুন" else "Add note",
                            style = AppTypographyScale.buttonText
                        )
                    }
                }
            }
        }

        // FULL LIST OF CREDIT SALES AND PAYMENTS (Explicit user requirement)
        Text(
            text = if (isBangla) "লেনদেন ও বাকির ইতিহাস (Credit Sales & Payments)" else "Credit Sales & Payments History",
            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            color = TextPrimary
        )

        if (ledgerEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "এখনও কোন বাকির হিসাব বা জমা রেকর্ড নেই।" else "No credit transactions or payments recorded yet.",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )
            }
        } else {
            val sdf = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
            ledgerEntries.forEach { entry ->
                LedgerItemCard(
                    entry = entry,
                    currentLanguage = currentLanguage,
                    sdf = sdf,
                    onEdit = { viewModel.openEditEntry(entry) },
                    onDelete = { viewModel.openDeleteEntry(entry) }
                )
            }
        }
    }
}

/**
 * Individual Ledger Entry Card (Sale or Payment) with Edit and Delete capabilities
 */
@Composable
fun LedgerItemCard(
    entry: CustomerLedgerItem,
    currentLanguage: AppLanguage,
    sdf: SimpleDateFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val isSale = entry is CustomerLedgerItem.Sale

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ledger_entry_${entry.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, if (isSale) Color(0xFFF59E0B).copy(alpha = 0.35f) else VividEmeraldAccent.copy(alpha = 0.35f))
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSale) Color(0x33F59E0B) else VividEmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSale) Icons.Default.Receipt else Icons.Default.Payment,
                            contentDescription = null,
                            tint = if (isSale) Color(0xFFF59E0B) else VividEmeraldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isSale) {
                                if (isBangla) "বাকিতে বিক্রয়" else "Credit Sale"
                            } else {
                                if (isBangla) "নগদ পরিশোধ / জমা" else "Payment Received"
                            },
                            style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = sdf.format(Date(entry.timestamp)),
                            style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                            color = TextMuted
                        )
                    }
                }

                // Amount
                Text(
                    text = if (isSale) "+ ${formatTaka(entry.amount, currentLanguage)}" else "- ${formatTaka(entry.amount, currentLanguage)}",
                    style = AppTypographyScale.moneyNumber.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isSale) Color(0xFFF59E0B) else VividEmeraldAccent
                )
            }

            // Note / Details
            if (entry.note.isNotBlank()) {
                Text(
                    text = entry.note,
                    style = AppTypographyScale.hintText,
                    color = TextPrimary
                )
            }

            // Edit and Delete actions row (Explicit user requirement)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp).testTag("btn_edit_entry_${entry.id}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Entry", tint = VividEmeraldAccent, modifier = Modifier.size(16.dp))
                }

                Spacer(Modifier.width(4.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("btn_delete_entry_${entry.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Entry", tint = LowStockRed, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

/**
 * Dialog: Add Customer (name required, phone, address, note)
 */
@Composable
fun AddCustomerDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, address: String, note: String, initialBaki: Double) -> Unit
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var initialBakiText by remember { mutableStateOf("0") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "নতুন গ্রাহক যুক্ত করুন" else "Add New Customer",
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
                    value = name,
                    onValueChange = {
                        name = it
                        showError = false
                    },
                    label = { Text(if (isBangla) "গ্রাহকের নাম (আবশ্যক) *" else "Customer Name (Required) *", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    isError = showError,
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_name"),
                    colors = dialogTextFieldColors()
                )
                if (showError) {
                    Text(
                        text = if (isBangla) "অনুগ্রহ করে গ্রাহকের নাম লিখুন" else "Customer name is required",
                        color = LowStockRed,
                        style = AppTypographyScale.hintText
                    )
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isBangla) "মোবাইল নম্বর" else "Phone Number", style = AppTypographyScale.hintText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_phone"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(if (isBangla) "ঠিকানা" else "Address", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_address"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = initialBakiText,
                    onValueChange = { initialBakiText = it },
                    label = { Text(if (isBangla) "পূর্বের বাকি (যদি থাকে ৳)" else "Initial Balance Owed (৳)", style = AppTypographyScale.hintText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_initial_baki"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (isBangla) "নোট / কাজের বিবরণ" else "Note / Description", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_note"),
                    colors = dialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val initialBaki = initialBakiText.toDoubleOrNull() ?: 0.0
                        onConfirm(name, phone, address, note, initialBaki)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.height(44.dp).testTag("btn_confirm_add_customer"),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(
                    text = if (isBangla) "সংরক্ষণ করুন" else "Save Customer",
                    style = AppTypographyScale.buttonText
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.height(44.dp)) {
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
 * Dialog: Record Payment (amount and note)
 */
@Composable
fun RecordCustomerPaymentDialog(
    customer: Customer,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, note: String) -> Unit
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var amountText by remember { mutableStateOf(if (customer.totalBaki > 0) customer.totalBaki.toLong().toString() else "500") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "গ্রাহকের বাকি পরিশোধ গ্রহণ" else "Record Payment",
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
                    text = "${customer.name} • ${if (isBangla) "বর্তমান বাকি:" else "Current Balance:"} ${formatTaka(customer.totalBaki, currentLanguage)}",
                    style = AppTypographyScale.hintText,
                    color = VividEmeraldAccent
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (isBangla) "পরিশোধের পরিমাণ (৳) *" else "Payment Amount (৳) *", style = AppTypographyScale.hintText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_payment_amount"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (isBangla) "নোট / রসিদ নম্বর" else "Note / Receipt Number", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_payment_note"),
                    colors = dialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount, note)
                    }
                },
                modifier = Modifier.height(44.dp).testTag("btn_confirm_customer_payment"),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(
                    text = if (isBangla) "জমা সম্পন্ন করুন" else "Confirm Payment",
                    style = AppTypographyScale.buttonText
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.height(44.dp)) {
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
 * Dialog: Add Note to customer
 */
@Composable
fun AddCustomerNoteDialog(
    customer: Customer,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (note: String) -> Unit
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "গ্রাহকের নোট যোগ করুন" else "Add Customer Note",
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
                    text = customer.name,
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(if (isBangla) "নতুন নোট লিখুন" else "Enter Note", style = AppTypographyScale.hintText) },
                    modifier = Modifier.fillMaxWidth().testTag("input_add_note_text"),
                    colors = dialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (noteText.isNotBlank()) {
                        onConfirm(noteText)
                    }
                },
                modifier = Modifier.height(44.dp).testTag("btn_confirm_add_note"),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(
                    text = if (isBangla) "নোট সংরক্ষণ" else "Save Note",
                    style = AppTypographyScale.buttonText
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.height(44.dp)) {
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
 * Dialog: Edit Ledger Entry (Payment or Credit Sale)
 */
@Composable
fun EditLedgerEntryDialog(
    entry: CustomerLedgerItem,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirmPaymentEdit: (newAmount: Double, newNote: String) -> Unit,
    onConfirmSaleEdit: (newDueAmount: Double, newNote: String, newDueDate: Long?) -> Unit
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    var amountText by remember { mutableStateOf(entry.amount.toLong().toString()) }
    var noteText by remember { mutableStateOf(entry.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (entry is CustomerLedgerItem.Sale) {
                    if (isBangla) "বাকি হিসাব সংশোধন (Edit Sale)" else "Edit Credit Sale Entry"
                } else {
                    if (isBangla) "পরিশোধ হিসাব সংশোধন (Edit Payment)" else "Edit Payment Entry"
                },
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = {
                        Text(
                            if (entry is CustomerLedgerItem.Sale) {
                                if (isBangla) "বকেয়া পরিমাণ (৳)" else "Due Balance Amount (৳)"
                            } else {
                                if (isBangla) "পরিশোধিত পরিমাণ (৳)" else "Paid Amount (৳)"
                            },
                            style = AppTypographyScale.hintText
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_edit_entry_amount"),
                    colors = dialogTextFieldColors()
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(if (isBangla) "নোট / বিবরণ" else "Note / Description", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_edit_entry_note"),
                    colors = dialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: entry.amount
                    if (entry is CustomerLedgerItem.Payment) {
                        onConfirmPaymentEdit(amount, noteText)
                    } else if (entry is CustomerLedgerItem.Sale) {
                        onConfirmSaleEdit(amount, noteText, entry.saleOrder.dueDate)
                    }
                },
                modifier = Modifier.height(44.dp).testTag("btn_confirm_edit_entry"),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(
                    text = if (isBangla) "সংশোধন সংরক্ষণ" else "Update Entry",
                    style = AppTypographyScale.buttonText
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.height(44.dp)) {
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
 * Dialog: Delete Entry Confirmation
 */
@Composable
fun DeleteLedgerEntryConfirmDialog(
    entry: CustomerLedgerItem,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val isSale = entry is CustomerLedgerItem.Sale

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "হিসাব এন্ট্রি মুছে ফেলবেন?" else "Delete Ledger Entry?",
                style = AppTypographyScale.screenTitle,
                color = LowStockRed
            )
        },
        text = {
            Text(
                text = if (isBangla)
                    "আপনি কি নিশ্চিত? এই এন্ট্রিটি মুছে ফেললে গ্রাহকের মোট বকেয়া বাকি স্বয়ংক্রিয়ভাবে পুনরায় হিসাব হবে।"
                else
                    "Are you sure you want to delete this entry? The customer's balance owed will be recalculated automatically.",
                style = AppTypographyScale.normalText,
                color = TextPrimary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.height(44.dp).testTag("btn_confirm_delete_entry"),
                colors = ButtonDefaults.buttonColors(containerColor = LowStockRed)
            ) {
                Text(
                    text = if (isBangla) "মুছে ফেলুন" else "Delete",
                    style = AppTypographyScale.buttonText
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.height(44.dp)) {
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
