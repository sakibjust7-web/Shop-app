package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.Customer
import com.example.data.local.entity.Product
import com.example.data.local.entity.SaleOrder
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.formatTaka
import com.example.ui.localization.toBengaliNumerals
import com.example.ui.screens.sell.CartItem
import com.example.ui.screens.sell.SellViewModel
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    viewModel: SellViewModel = viewModel()
) {
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val selectedProductId by viewModel.selectedProductId.collectAsStateWithLifecycle()
    val isCreditSale by viewModel.isCreditSale.collectAsStateWithLifecycle()
    val selectedCustomer by viewModel.selectedCustomer.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val paidNowInput by viewModel.paidNowInput.collectAsStateWithLifecycle()
    val dueDateDays by viewModel.dueDateDays.collectAsStateWithLifecycle()
    val showBatchSalesSheet by viewModel.showBatchSalesSheet.collectAsStateWithLifecycle()
    val showNewCustomerDialog by viewModel.showNewCustomerDialog.collectAsStateWithLifecycle()
    val completedSaleOrder by viewModel.completedSaleOrder.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val totalBill by viewModel.totalBill.collectAsStateWithLifecycle()
    val totalCost by viewModel.totalCost.collectAsStateWithLifecycle()
    val totalProfit by viewModel.totalProfit.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle error message snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearErrorMessage()
            }
        }
    }

    // Currently focused item in the lower panel
    val activeItem: CartItem? = cartItems.find { it.product.id == selectedProductId }
        ?: cartItems.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSlateBackground)
            .testTag("screen_sell")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Search Box matching reference image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sell_search_input"),
                    placeholder = {
                        Text(
                            text = if (isBangla) "হার্ডওয়্যার পণ্য খুঁজুন..." else "Search hardware items...",
                            style = AppTypographyScale.normalText,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSlateSurfaceVariant,
                        unfocusedContainerColor = DarkSlateSurfaceVariant,
                        focusedBorderColor = VividEmeraldPrimary,
                        unfocusedBorderColor = DarkSlateCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true,
                    textStyle = AppTypographyScale.normalText
                )
            }

            // 2. Upper 3-Column Product Picker Grid (matching reference image)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBangla) "কোনো পণ্য পাওয়া যায়নি" else "No hardware items found",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("sell_product_grid"),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products, key = { it.id }) { product ->
                            val isSelected = selectedProductId == product.id
                            SellProductGridCard(
                                product = product,
                                isSelected = isSelected,
                                isBangla = isBangla,
                                onSelect = {
                                    viewModel.selectProduct(product)
                                }
                            )
                        }
                    }
                }
            }

            // 3. Lower Active Product & Checkout Panel (Matching the Reference Image)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sell_active_panel"),
                color = DarkSlateSurface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (activeItem != null) {
                        // Product Identity Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Thumbnail Box matching image
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                HardwareItemThumbnail(
                                    photoKey = activeItem.product.photoKey,
                                    category = activeItem.product.category,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeItem.product.localizedName(isBangla),
                                    style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                val leftNum = if (isBangla) toBengaliNumerals(activeItem.product.stockQuantity.toString()) else activeItem.product.stockQuantity.toString()
                                val unitStr = activeItem.product.localizedUnit(isBangla)
                                Text(
                                    text = if (isBangla) "$leftNum $unitStr বাকি" else "$leftNum $unitStr left",
                                    style = AppTypographyScale.hintText,
                                    color = if (activeItem.product.isLowStock) LowStockRed else Color(0xFFF59E0B),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Quantity Stepper and Price Per Piece Fields
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Column: Quantity Stepper
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isBangla) "পরিমাণ" else "Quantity",
                                    style = AppTypographyScale.hintText,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSlateSurfaceVariant,
                                    border = BorderStroke(1.dp, DarkSlateCardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (activeItem.quantity > 1) {
                                                    viewModel.updateActiveQuantity(activeItem.quantity - 1)
                                                }
                                            },
                                            enabled = activeItem.quantity > 1,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .testTag("sell_qty_decrement")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Remove,
                                                contentDescription = "Decrease",
                                                tint = if (activeItem.quantity > 1) TextPrimary else TextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        val qtyDisplay = if (isBangla) toBengaliNumerals(activeItem.quantity.toString()) else activeItem.quantity.toString()
                                        Text(
                                            text = qtyDisplay,
                                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary,
                                            textAlign = TextAlign.Center
                                        )

                                        IconButton(
                                            onClick = {
                                                if (activeItem.quantity < activeItem.product.stockQuantity) {
                                                    viewModel.updateActiveQuantity(activeItem.quantity + 1)
                                                }
                                            },
                                            enabled = activeItem.quantity < activeItem.product.stockQuantity,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .testTag("sell_qty_increment")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Increase",
                                                tint = if (activeItem.quantity < activeItem.product.stockQuantity) TextPrimary else TextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Right Column: Price per piece (Editable)
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isBangla) "প্রতি পিস মূল্য" else "Price per piece",
                                    style = AppTypographyScale.hintText,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                var priceText by remember(activeItem.product.id, activeItem.unitPrice) {
                                    mutableStateOf(
                                        if (activeItem.unitPrice % 1.0 == 0.0) activeItem.unitPrice.toLong().toString()
                                        else activeItem.unitPrice.toString()
                                    )
                                }

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSlateSurfaceVariant,
                                    border = BorderStroke(1.dp, DarkSlateCardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "৳",
                                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                                            color = VividEmeraldAccent,
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                        OutlinedTextField(
                                            value = priceText,
                                            onValueChange = { input ->
                                                priceText = input
                                                val parsed = input.toDoubleOrNull()
                                                if (parsed != null && parsed >= 0) {
                                                    viewModel.updateActivePrice(parsed)
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("sell_price_input"),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent,
                                                focusedBorderColor = Color.Transparent,
                                                unfocusedBorderColor = Color.Transparent,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            ),
                                            textStyle = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Empty Selection Hint
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBangla) "গ্রিড থেকে বিক্রির জন্য পণ্য বেছে নিন" else "Tap a product above to add to cart",
                                style = AppTypographyScale.normalText,
                                color = TextMuted
                            )
                        }
                    }

                    // Payment Option Segmented Control (Matching reference: Paid now vs Pay later)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "Paid now" Button
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.setCreditSale(false) }
                                .testTag("sell_pay_now_tab"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isCreditSale) VividEmeraldPrimary else DarkSlateSurfaceVariant,
                            border = if (!isCreditSale) null else BorderStroke(1.dp, DarkSlateCardBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isBangla) "পরিশোধিত" else "Paid now",
                                    style = AppTypographyScale.buttonText.copy(
                                        fontWeight = if (!isCreditSale) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (!isCreditSale) Color.White else TextMuted
                                )
                            }
                        }

                        // "Pay later" Button (Baki / Credit)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.setCreditSale(true) }
                                .testTag("sell_pay_later_tab"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCreditSale) VividEmeraldPrimary else DarkSlateSurfaceVariant,
                            border = if (isCreditSale) null else BorderStroke(1.dp, DarkSlateCardBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isBangla) "বাকি (ক্রেডিট)" else "Pay later",
                                    style = AppTypographyScale.buttonText.copy(
                                        fontWeight = if (isCreditSale) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isCreditSale) Color.White else TextMuted
                                )
                            }
                        }
                    }

                    // If Credit Sale is selected: Customer Selector and Paid Amount
                    if (isCreditSale) {
                        CreditDetailsCard(
                            isBangla = isBangla,
                            selectedCustomer = selectedCustomer,
                            allCustomers = allCustomers,
                            paidNowInput = paidNowInput,
                            totalBill = totalBill,
                            dueDateDays = dueDateDays,
                            onCustomerSelected = { viewModel.selectCustomer(it) },
                            onPaidNowChanged = { viewModel.setPaidNowInput(it) },
                            onDueDateChanged = { viewModel.setDueDateDays(it) },
                            onOpenNewCustomerDialog = { viewModel.openNewCustomerDialog() }
                        )
                    }

                    // Financial Summary Card (Total | Cost | Profit) matching image
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_financial_card"),
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSlateSurfaceVariant,
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Column 1: Total
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isBangla) "মোট বিল" else "Total",
                                    style = AppTypographyScale.hintText,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = formatTaka(totalBill, currentLanguage),
                                    style = AppTypographyScale.moneyNumber.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = VividEmeraldAccent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Vertical Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(32.dp)
                                    .background(DarkSlateCardBorder)
                            )

                            // Column 2: Cost
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isBangla) "ক্রয়মূল্য" else "Cost",
                                    style = AppTypographyScale.hintText,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = formatTaka(totalCost, currentLanguage),
                                    style = AppTypographyScale.normalText.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Vertical Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(32.dp)
                                    .background(DarkSlateCardBorder)
                            )

                            // Column 3: Profit
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
                                    text = formatTaka(totalProfit, currentLanguage),
                                    style = AppTypographyScale.normalText.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = VividEmeraldAccent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Primary Button: "Confirm Sale" (Green 44dp)
                    Button(
                        onClick = { viewModel.completeSale(isBangla) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sell_confirm_sale_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VividEmeraldPrimary
                        ),
                        enabled = cartItems.isNotEmpty()
                    ) {
                        Text(
                            text = if (isBangla) "বিক্রি নিশ্চিত করুন" else "Confirm Sale",
                            style = AppTypographyScale.buttonText.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    // Secondary Button: "Batch Sales" / Cart Drawer
                    OutlinedButton(
                        onClick = { viewModel.openBatchSales() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sell_batch_sales_button"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, DarkSlateCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingBag,
                                contentDescription = null,
                                tint = VividEmeraldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            val cartCount = cartItems.size
                            val countStr = if (isBangla) toBengaliNumerals(cartCount.toString()) else cartCount.toString()
                            Text(
                                text = if (cartCount > 1) {
                                    if (isBangla) "কার্ট তালিকা ($countStr পণ্য)" else "Batch Sales ($countStr items)"
                                } else {
                                    if (isBangla) "কার্ট ভিউ / একাধিক পণ্য" else "Batch Sales"
                                },
                                style = AppTypographyScale.buttonText,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Snackbar host on top
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }

    // Modal Sheet for Batch Sales / Cart
    if (showBatchSalesSheet) {
        BatchSalesBottomSheet(
            isBangla = isBangla,
            currentLanguage = currentLanguage,
            cartItems = cartItems,
            totalBill = totalBill,
            totalCost = totalCost,
            totalProfit = totalProfit,
            onUpdateQuantity = { id, qty -> viewModel.updateCartItemQuantity(id, qty) },
            onUpdatePrice = { id, price -> viewModel.updateCartItemPrice(id, price) },
            onRemoveItem = { id -> viewModel.removeCartItem(id) },
            onClearCart = { viewModel.clearCart() },
            onCompleteSale = {
                viewModel.closeBatchSales()
                viewModel.completeSale(isBangla)
            },
            onDismiss = { viewModel.closeBatchSales() }
        )
    }

    // Dialog for Adding a New Customer
    if (showNewCustomerDialog) {
        AddNewCustomerDialog(
            isBangla = isBangla,
            onSave = { name, phone, addr ->
                viewModel.addNewCustomer(name, phone, addr)
            },
            onDismiss = { viewModel.closeNewCustomerDialog() }
        )
    }

    // Dialog for Successful Sale Confirmation
    completedSaleOrder?.let { order ->
        SaleSuccessDialog(
            order = order,
            currentLanguage = currentLanguage,
            isBangla = isBangla,
            onDismiss = { viewModel.dismissSuccessDialog() }
        )
    }
}

/**
 * 3-Column Product Grid Card matching the reference image
 */
@Composable
private fun SellProductGridCard(
    product: Product,
    isSelected: Boolean,
    isBangla: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onSelect)
            .testTag("sell_product_card_${product.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) VividEmeraldPrimary else DarkSlateCardBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // White photo thumbnail box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                HardwareItemThumbnail(
                    photoKey = product.photoKey,
                    category = product.category,
                    modifier = Modifier.size(36.dp)
                )

                if (product.isLowStock) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = LowStockRed
                    ) {
                        Text(
                            text = if (isBangla) "কম" else "Low",
                            style = AppTypographyScale.hintText.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Product Name (1 line, ellipsis)
            Text(
                text = product.localizedName(isBangla),
                style = AppTypographyScale.normalText.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Stock Count (e.g., Stock: 4 / স্টক: ৪)
            val stockNum = if (isBangla) toBengaliNumerals(product.stockQuantity.toString()) else product.stockQuantity.toString()
            Text(
                text = if (isBangla) "স্টক: $stockNum" else "Stock: $stockNum",
                style = AppTypographyScale.hintText,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Customer & Due details card when "Pay later" is active
 */
@Composable
private fun CreditDetailsCard(
    isBangla: Boolean,
    selectedCustomer: Customer?,
    allCustomers: List<Customer>,
    paidNowInput: String,
    totalBill: Double,
    dueDateDays: Int?,
    onCustomerSelected: (Customer) -> Unit,
    onPaidNowChanged: (String) -> Unit,
    onDueDateChanged: (Int?) -> Unit,
    onOpenNewCustomerDialog: () -> Unit
) {
    var customerMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("credit_details_card"),
        shape = RoundedCornerShape(10.dp),
        color = DarkSlateSurfaceVariant,
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Customer Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "বাকির খাতা / গ্রাহক" else "Baki Customer",
                    style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                    color = VividEmeraldAccent
                )

                // "+ Add New Customer" button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onOpenNewCustomerDialog)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Add Customer",
                        tint = VividEmeraldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isBangla) "+ নতুন গ্রাহক" else "+ New Customer",
                        style = AppTypographyScale.hintText,
                        color = VividEmeraldAccent
                    )
                }
            }

            // Customer Dropdown Box
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { customerMenuExpanded = true }
                        .testTag("customer_dropdown_trigger"),
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSlateSurface,
                    border = BorderStroke(1.dp, DarkSlateCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (selectedCustomer != null) VividEmeraldAccent else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = selectedCustomer?.name
                                    ?: (if (isBangla) "গ্রাহক নির্বাচন করুন" else "Select Customer"),
                                style = AppTypographyScale.normalText,
                                color = if (selectedCustomer != null) TextPrimary else TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (selectedCustomer != null) {
                            val bakiStr = if (isBangla) toBengaliNumerals(selectedCustomer.totalBaki.toInt().toString()) else selectedCustomer.totalBaki.toInt().toString()
                            Text(
                                text = if (isBangla) "পূর্ব বাকি ৳$bakiStr" else "Baki: ৳$bakiStr",
                                style = AppTypographyScale.hintText,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = customerMenuExpanded,
                    onDismissRequest = { customerMenuExpanded = false },
                    modifier = Modifier.background(DarkSlateSurface)
                ) {
                    if (allCustomers.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (isBangla) "কোনো গ্রাহক নেই, নতুন যোগ করুন" else "No customers, add one",
                                    style = AppTypographyScale.hintText,
                                    color = TextMuted
                                )
                            },
                            onClick = {
                                customerMenuExpanded = false
                                onOpenNewCustomerDialog()
                            }
                        )
                    } else {
                        allCustomers.forEach { cust ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = cust.name,
                                            style = AppTypographyScale.normalText,
                                            color = TextPrimary
                                        )
                                        val bakiNum = if (isBangla) toBengaliNumerals(cust.totalBaki.toInt().toString()) else cust.totalBaki.toInt().toString()
                                        Text(
                                            text = "${cust.phone} • ${if (isBangla) "বাকি: ৳$bakiNum" else "Baki: ৳$bakiNum"}",
                                            style = AppTypographyScale.hintText,
                                            color = TextMuted
                                        )
                                    }
                                },
                                onClick = {
                                    onCustomerSelected(cust)
                                    customerMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Paid Now Amount and Due Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Paid now amount input (can be 0)
                OutlinedTextField(
                    value = paidNowInput,
                    onValueChange = onPaidNowChanged,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("credit_paid_now_input"),
                    label = {
                        Text(
                            text = if (isBangla) "জমা (৳)" else "Paid Now (৳)",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    },
                    placeholder = {
                        Text("0", style = AppTypographyScale.normalText, color = TextMuted)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSlateSurface,
                        unfocusedContainerColor = DarkSlateSurface,
                        focusedBorderColor = VividEmeraldPrimary,
                        unfocusedBorderColor = DarkSlateCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    textStyle = AppTypographyScale.normalText
                )

                // Due balance preview
                val enteredPaid = paidNowInput.toDoubleOrNull() ?: 0.0
                val remainingBaki = (totalBill - enteredPaid).coerceAtLeast(0.0)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSlateSurface,
                    border = BorderStroke(1.dp, DarkSlateCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isBangla) "নতুন বাকি হবে" else "Due (Baki)",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        val bakiNum = if (isBangla) toBengaliNumerals(remainingBaki.toInt().toString()) else remainingBaki.toInt().toString()
                        Text(
                            text = "৳ $bakiNum",
                            style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                            color = LowStockRed
                        )
                    }
                }
            }

            // Due Date Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "মেয়াদ:" else "Due:",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )

                listOf(7 to "7D", 15 to "15D", 30 to "30D").forEach { (days, label) ->
                    val isSelected = dueDateDays == days
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                onDueDateChanged(if (isSelected) null else days)
                            },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) VividEmeraldPrimary else DarkSlateSurface,
                        border = BorderStroke(1.dp, if (isSelected) VividEmeraldPrimary else DarkSlateCardBorder)
                    ) {
                        Text(
                            text = if (isBangla) {
                                when (days) {
                                    7 -> "৭ দিন"
                                    15 -> "১৫ দিন"
                                    else -> "১ মাস"
                                }
                            } else label,
                            style = AppTypographyScale.hintText.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom Sheet for Batch Sales (Multi-Product Cart)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BatchSalesBottomSheet(
    isBangla: Boolean,
    currentLanguage: AppLanguage,
    cartItems: List<CartItem>,
    totalBill: Double,
    totalCost: Double,
    totalProfit: Double,
    onUpdateQuantity: (Long, Int) -> Unit,
    onUpdatePrice: (Long, Double) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onClearCart: () -> Unit,
    onCompleteSale: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSlateSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = VividEmeraldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isBangla) "কার্ট তালিকা (ব্যাচ সেল)" else "Cart Items (Batch Sale)",
                        style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                if (cartItems.isNotEmpty()) {
                    Text(
                        text = if (isBangla) "সব মুছুন" else "Clear All",
                        style = AppTypographyScale.hintText,
                        color = LowStockRed,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onClearCart)
                            .padding(4.dp)
                    )
                }
            }

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) "কার্টে কোনো পণ্য নেই" else "No items in cart",
                        style = AppTypographyScale.normalText,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems, key = { it.product.id }) { item ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSlateSurfaceVariant,
                            border = BorderStroke(1.dp, DarkSlateCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    HardwareItemThumbnail(
                                        photoKey = item.product.photoKey,
                                        category = item.product.category,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                // Name & Unit Price
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.product.localizedName(isBangla),
                                        style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium),
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${formatTaka(item.unitPrice, currentLanguage)} / ${item.product.localizedUnit(isBangla)}",
                                        style = AppTypographyScale.hintText,
                                        color = TextMuted
                                    )
                                }

                                // Stepper
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (item.quantity > 1) {
                                                onUpdateQuantity(item.product.id, item.quantity - 1)
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Minus",
                                            tint = TextPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    val qtyStr = if (isBangla) toBengaliNumerals(item.quantity.toString()) else item.quantity.toString()
                                    Text(
                                        text = qtyStr,
                                        style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )

                                    IconButton(
                                        onClick = {
                                            if (item.quantity < item.product.stockQuantity) {
                                                onUpdateQuantity(item.product.id, item.quantity + 1)
                                            }
                                        },
                                        modifier = Modifier.size(28.dp),
                                        enabled = item.quantity < item.product.stockQuantity
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Plus",
                                            tint = if (item.quantity < item.product.stockQuantity) TextPrimary else TextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                // Line Total
                                Text(
                                    text = formatTaka(item.lineTotal, currentLanguage),
                                    style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                    color = VividEmeraldAccent,
                                    modifier = Modifier.width(70.dp),
                                    textAlign = TextAlign.End
                                )

                                // Delete
                                IconButton(
                                    onClick = { onRemoveItem(item.product.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Remove",
                                        tint = LowStockRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Summary
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = DarkSlateBackground,
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "মোট বিক্রয়মূল্য" else "Total Amount",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        Text(
                            text = formatTaka(totalBill, currentLanguage),
                            style = AppTypographyScale.moneyNumber.copy(fontSize = 18.sp),
                            color = VividEmeraldAccent
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isBangla) "প্রত্যাশিত লাভ" else "Estimated Profit",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        Text(
                            text = formatTaka(totalProfit, currentLanguage),
                            style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                            color = VividEmeraldAccent
                        )
                    }
                }
            }

            // Action Buttons
            Button(
                onClick = onCompleteSale,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("batch_sale_confirm_btn"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                enabled = cartItems.isNotEmpty()
            ) {
                Text(
                    text = if (isBangla) "এই বিক্রি সম্পন্ন করুন" else "Complete Batch Sale",
                    style = AppTypographyScale.buttonText.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Text(
                    text = if (isBangla) "আরও পণ্য যোগ করতে ফিরে যান" else "Add More Items",
                    style = AppTypographyScale.buttonText,
                    color = TextPrimary
                )
            }
        }
    }
}

/**
 * Dialog to add a new customer quickly from the sell screen
 */
@Composable
private fun AddNewCustomerDialog(
    isBangla: Boolean,
    onSave: (name: String, phone: String, address: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "নতুন গ্রাহক যোগ করুন" else "Add New Customer",
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBangla) "গ্রাহকের নাম *" else "Customer Name *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_customer_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSlateSurfaceVariant,
                        unfocusedContainerColor = DarkSlateSurfaceVariant,
                        focusedBorderColor = VividEmeraldPrimary,
                        unfocusedBorderColor = DarkSlateCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isBangla) "মোবাইল নম্বর" else "Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_customer_phone_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSlateSurfaceVariant,
                        unfocusedContainerColor = DarkSlateSurfaceVariant,
                        focusedBorderColor = VividEmeraldPrimary,
                        unfocusedBorderColor = DarkSlateCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(if (isBangla) "ঠিকানা / এলাকা" else "Address / Area") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_customer_addr_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSlateSurfaceVariant,
                        unfocusedContainerColor = DarkSlateSurfaceVariant,
                        focusedBorderColor = VividEmeraldPrimary,
                        unfocusedBorderColor = DarkSlateCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, phone, address)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                modifier = Modifier
                    .height(44.dp)
                    .testTag("save_new_customer_btn")
            ) {
                Text(
                    text = if (isBangla) "সংরক্ষণ করুন" else "Save Customer",
                    style = AppTypographyScale.buttonText,
                    color = Color.White
                )
            }
        },
        dismissButton = {
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
 * Dialog displaying invoice receipt after sale completion
 */
@Composable
private fun SaleSuccessDialog(
    order: SaleOrder,
    currentLanguage: AppLanguage,
    isBangla: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(VividEmeraldPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = VividEmeraldAccent,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = if (isBangla) "বিক্রি সফল হয়েছে!" else "Sale Completed!",
                style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Invoice Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBangla) "ইনভয়েস নং:" else "Invoice No:",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                    Text(
                        text = order.invoiceNumber,
                        style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                // Customer Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBangla) "গ্রাহক:" else "Customer:",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                    Text(
                        text = order.customerName,
                        style = AppTypographyScale.normalText,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Total Bill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBangla) "মোট বিল:" else "Total Bill:",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                    Text(
                        text = formatTaka(order.totalAmount, currentLanguage),
                        style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                        color = VividEmeraldAccent
                    )
                }

                // Paid Amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBangla) "পরিশোধিত:" else "Paid Amount:",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                    Text(
                        text = formatTaka(order.paidAmount, currentLanguage),
                        style = AppTypographyScale.normalText,
                        color = TextPrimary
                    )
                }

                // Due Baki (if any)
                if (order.dueAmount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "বাকি (ক্রেডিট):" else "Due (Baki):",
                            style = AppTypographyScale.hintText,
                            color = LowStockRed
                        )
                        Text(
                            text = formatTaka(order.dueAmount, currentLanguage),
                            style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                            color = LowStockRed
                        )
                    }
                }

                // Profit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBangla) "অর্জিত লাভ:" else "Profit Earned:",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                    Text(
                        text = formatTaka(order.profit, currentLanguage),
                        style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                        color = VividEmeraldAccent
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("sale_success_ok_btn")
            ) {
                Text(
                    text = if (isBangla) "নতুন বিক্রি শুরু করুন" else "Done (New Sale)",
                    style = AppTypographyScale.buttonText.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    )
}

/**
 * Thumbnail icon selector for hardware products
 */
@Composable
private fun HardwareItemThumbnail(
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
