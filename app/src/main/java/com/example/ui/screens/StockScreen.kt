package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.local.entity.Category
import com.example.data.local.entity.Product
import com.example.data.local.entity.Supplier
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.formatTaka
import com.example.ui.localization.toBengaliNumerals
import com.example.ui.screens.stock.StockViewModel
import com.example.ui.theme.AppTypographyScale
import com.example.ui.theme.DarkSlateBackground
import com.example.ui.theme.DarkSlateCardBorder
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceVariant
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.OutlinedButton
import com.example.ui.theme.LowStockRedContainer
import com.example.ui.theme.LowStockRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VividEmeraldAccent
import com.example.ui.theme.VividEmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    viewModel: StockViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isGridView by viewModel.isGridView.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalProductCount.collectAsStateWithLifecycle()

    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val showAddDialog by viewModel.showAddProductDialog.collectAsStateWithLifecycle()
    val productToEdit by viewModel.productToEdit.collectAsStateWithLifecycle()
    val productForAddStock by viewModel.productForAddStock.collectAsStateWithLifecycle()
    val showCategoryManager by viewModel.showCategoryManager.collectAsStateWithLifecycle()
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()

    val isBangla = currentLanguage == AppLanguage.BANGLA

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSlateBackground)
            .testTag("screen_stock")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stock Screen Header: "Stock" on left, product count and view toggle on right
            StockHeaderRow(
                totalCount = totalCount,
                isGridView = isGridView,
                onToggleView = { viewModel.toggleViewMode() },
                onManageCategories = { viewModel.openCategoryManager() },
                isBangla = isBangla
            )

            // Search Box (matching reference image)
            StockSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                isBangla = isBangla
            )

            // Category Chips Row (matching reference image)
            CategoryChipsRow(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelect = { viewModel.onCategorySelected(it) },
                isBangla = isBangla
            )

            // Products Content (Grid or Compact List)
            if (products.isEmpty()) {
                EmptyProductsView(isBangla = isBangla)
            } else if (isGridView) {
                // 3-Column Card Grid matching reference image
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("stock_grid_view"),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductGridCard(
                            product = product,
                            currentLanguage = currentLanguage,
                            isBangla = isBangla,
                            onClick = { viewModel.selectProduct(product) }
                        )
                    }
                }
            } else {
                // Compact List View as requested in text prompt
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("stock_list_view"),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductListRow(
                            product = product,
                            currentLanguage = currentLanguage,
                            isBangla = isBangla,
                            onClick = { viewModel.selectProduct(product) }
                        )
                    }
                }
            }
        }

        // Green "+" Floating Action Button (FAB) matching reference image
        FloatingActionButton(
            onClick = { viewModel.openAddProductDialog() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(56.dp)
                .testTag("fab_add_product"),
            shape = CircleShape,
            containerColor = VividEmeraldPrimary,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Product",
                modifier = Modifier.size(28.dp)
            )
        }

        // Product Detail Bottom Sheet (when tapping a product)
        selectedProduct?.let { product ->
            ProductDetailBottomSheet(
                product = product,
                currentLanguage = currentLanguage,
                isBangla = isBangla,
                onDismiss = { viewModel.selectProduct(null) },
                onAddStock = {
                    viewModel.openAddStock(product)
                },
                onEdit = {
                    viewModel.selectProduct(null)
                    viewModel.openEditProduct(product)
                },
                onDelete = {
                    viewModel.softDeleteProduct(product.id)
                }
            )
        }

        // Add Product Dialog
        if (showAddDialog) {
            AddEditProductDialog(
                title = if (isBangla) "নতুন পণ্য যোগ করুন" else "Add New Product",
                categories = allCategories,
                existingProduct = null,
                isBangla = isBangla,
                onDismiss = { viewModel.closeAddProductDialog() },
                onSave = { nameBn, nameEn, cat, desc, photo, qty, buy, sell, lowLimit, unitBn, unitEn ->
                    viewModel.saveNewProduct(
                        nameBn, nameEn, cat, desc, photo, qty, buy, sell, lowLimit, unitBn, unitEn
                    )
                }
            )
        }

        // Edit Product Dialog
        productToEdit?.let { product ->
            AddEditProductDialog(
                title = if (isBangla) "পণ্য সম্পাদনা" else "Edit Product",
                categories = allCategories,
                existingProduct = product,
                isBangla = isBangla,
                onDismiss = { viewModel.closeEditProduct() },
                onSave = { nameBn, nameEn, cat, desc, photo, qty, buy, sell, lowLimit, unitBn, unitEn ->
                    viewModel.updateExistingProduct(
                        product.copy(
                            nameBn = nameBn,
                            nameEn = nameEn,
                            category = cat,
                            description = desc,
                            photoKey = photo,
                            stockQuantity = qty,
                            buyPrice = buy,
                            sellPrice = sell,
                            lowStockThreshold = lowLimit,
                            unitBn = unitBn,
                            unitEn = unitEn
                        )
                    )
                }
            )
        }

        // Add Stock Dialog (Records a Purchase)
        productForAddStock?.let { product ->
            AddStockPurchaseDialog(
                product = product,
                isBangla = isBangla,
                suppliers = suppliers,
                onDismiss = { viewModel.closeAddStock() },
                onConfirm = { addedQty, unitPrice, supplierId, supplierName, paidAmount, dueAmount, billPhotoUri, note ->
                    viewModel.recordAddStock(
                        productId = product.id,
                        productName = product.nameEn,
                        addedQty = addedQty,
                        unitBuyingPrice = unitPrice,
                        supplierId = supplierId,
                        supplierName = supplierName,
                        paidAmount = paidAmount,
                        dueAmount = dueAmount,
                        billPhotoUri = billPhotoUri,
                        note = note
                    )
                }
            )
        }

        // Category Manager Sheet (Add, rename, hide, reorder)
        if (showCategoryManager) {
            CategoryManagerSheet(
                categories = allCategories,
                isBangla = isBangla,
                onDismiss = { viewModel.closeCategoryManager() },
                onAddCategory = { en, bn, emoji -> viewModel.addCategory(en, bn, emoji) },
                onUpdateCategory = { cat -> viewModel.updateCategory(cat) },
                onToggleVisibility = { cat -> viewModel.toggleCategoryVisibility(cat) },
                onDeleteCategory = { cat -> viewModel.deleteCategory(cat) }
            )
        }
    }
}

/**
 * Top Header: Screen Title, Total Products Count, View Mode Toggle, and Category Settings
 */
@Composable
private fun StockHeaderRow(
    totalCount: Int,
    isGridView: Boolean,
    onToggleView: () -> Unit,
    onManageCategories: () -> Unit,
    isBangla: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isBangla) "স্টক ইনভেন্টরি" else "Stock",
            style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val countText = if (isBangla) {
                "${toBengaliNumerals(totalCount.toString())} টি পণ্য"
            } else {
                "$totalCount products"
            }
            Text(
                text = countText,
                style = AppTypographyScale.hintText,
                color = TextMuted
            )

            // Category Management button
            IconButton(
                onClick = onManageCategories,
                modifier = Modifier.size(34.dp).testTag("btn_manage_categories")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Manage Categories",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // View Mode Toggle (Grid vs List)
            IconButton(
                onClick = onToggleView,
                modifier = Modifier.size(34.dp).testTag("btn_toggle_view_mode")
            ) {
                Icon(
                    imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                    contentDescription = "Toggle View",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Rounded Pill Search Bar matching reference image
 */
@Composable
private fun StockSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isBangla: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, DarkSlateCardBorder, RoundedCornerShape(22.dp)),
        color = DarkSlateSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_search_stock"),
                placeholder = {
                    Text(
                        text = if (isBangla) "পণ্য খুঁজুন..." else "Search stock items...",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                },
                textStyle = AppTypographyScale.normalText.copy(color = TextPrimary),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Horizontal Category Chips Row matching reference image
 */
@Composable
private fun CategoryChipsRow(
    categories: List<Category>,
    selectedCategory: String?,
    onCategorySelect: (String?) -> Unit,
    isBangla: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All" Chip
        val isAllSelected = selectedCategory == null || selectedCategory == "All"
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .clickable { onCategorySelect(null) }
                .testTag("category_chip_all"),
            shape = RoundedCornerShape(18.dp),
            color = if (isAllSelected) VividEmeraldPrimary else DarkSlateSurfaceVariant
        ) {
            Text(
                text = if (isBangla) "সব" else "All",
                style = AppTypographyScale.hintText.copy(
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isAllSelected) Color.White else TextMuted,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            )
        }

        // Category Chips
        categories.forEach { cat ->
            val isSelected = selectedCategory == cat.nameEn
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        onCategorySelect(if (isSelected) null else cat.nameEn)
                    }
                    .testTag("category_chip_${cat.id}"),
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected) VividEmeraldPrimary else DarkSlateSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cat.emoji,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cat.localizedName(isBangla),
                        style = AppTypographyScale.hintText.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) Color.White else TextMuted
                    )
                }
            }
        }
    }
}

/**
 * 3-Column Product Card matching the reference image
 */
@Composable
private fun ProductGridCard(
    product: Product,
    currentLanguage: AppLanguage,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    val isLowStock = product.isLowStock

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isLowStock) 1.dp else 1.dp,
                color = if (isLowStock) LowStockRed.copy(alpha = 0.8f) else DarkSlateCardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .testTag("product_grid_item_${product.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Photo Container with white/clean background and "Low" badge if low stock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                HardwareThumbnailIcon(
                    photoKey = product.photoKey,
                    category = product.category,
                    modifier = Modifier.size(36.dp)
                )

                // Red "Low" pill badge on top-right (matching image)
                if (isLowStock) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = LowStockRed
                    ) {
                        Text(
                            text = if (isBangla) "কম" else "Low",
                            style = AppTypographyScale.hintText.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Product Name (1 line, ellipsis)
            Text(
                text = product.localizedName(isBangla),
                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Quantity & Selling Price in ৳
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val qtyNum = if (isBangla) toBengaliNumerals(product.stockQuantity.toString()) else product.stockQuantity.toString()
                Text(
                    text = if (isBangla) "$qtyNum টি" else "Qty $qtyNum",
                    style = AppTypographyScale.hintText,
                    color = if (isLowStock) LowStockRed else TextMuted,
                    maxLines = 1
                )

                val priceFormatted = if (isBangla) {
                    "৳" + toBengaliNumerals(product.sellPrice.toLong().toString())
                } else {
                    "৳" + product.sellPrice.toLong().toString()
                }
                Text(
                    text = priceFormatted,
                    style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                    color = if (isLowStock) LowStockRed else VividEmeraldAccent,
                    maxLines = 1
                )
            }

            if (com.example.data.local.AppSettings.viewMode == com.example.data.local.ViewMode.DETAILED) {
                val margin = (product.sellPrice - product.buyPrice).toLong()
                val marginText = if (isBangla) "লাভ: ৳${toBengaliNumerals(margin.toString())}" else "Prof: ৳$margin"
                Text(
                    text = marginText,
                    style = AppTypographyScale.hintText.copy(fontSize = 10.sp),
                    color = VividEmeraldAccent.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Compact Product Row (List View as described in text prompt)
 */
@Composable
private fun ProductListRow(
    product: Product,
    currentLanguage: AppLanguage,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    val isLowStock = product.isLowStock

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isLowStock) LowStockRed.copy(alpha = 0.5f) else DarkSlateCardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .testTag("product_list_item_${product.id}"),
        shape = RoundedCornerShape(12.dp),
        color = DarkSlateSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Small 48dp photo container
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                color = Color.White
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    HardwareThumbnailIcon(
                        photoKey = product.photoKey,
                        category = product.category,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Name on one line and Category in small grey text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.localizedName(isBangla),
                    style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.category,
                    style = AppTypographyScale.hintText,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (com.example.data.local.AppSettings.viewMode == com.example.data.local.ViewMode.DETAILED) {
                    val margin = product.sellPrice - product.buyPrice
                    val marginPct = if (product.buyPrice > 0) ((margin / product.buyPrice) * 100).toInt() else 0
                    val marginText = if (isBangla) {
                        "ক্রয়: ৳${toBengaliNumerals(product.buyPrice.toLong().toString())} • লাভ: +$marginPct%"
                    } else {
                        "Cost: ৳${product.buyPrice.toLong()} • Margin: +$marginPct%"
                    }
                    Text(
                        text = marginText,
                        style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                        color = VividEmeraldAccent.copy(alpha = 0.85f),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Quantity in stock and selling price
            Column(horizontalAlignment = Alignment.End) {
                val priceFormatted = if (isBangla) {
                    "৳ " + toBengaliNumerals(product.sellPrice.toLong().toString())
                } else {
                    "৳ " + product.sellPrice.toLong().toString()
                }
                Text(
                    text = priceFormatted,
                    style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                    color = VividEmeraldAccent,
                    maxLines = 1
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val qtyNum = if (isBangla) toBengaliNumerals(product.stockQuantity.toString()) else product.stockQuantity.toString()
                    val unitStr = product.localizedUnit(isBangla)
                    Text(
                        text = "$qtyNum $unitStr",
                        style = AppTypographyScale.hintText,
                        color = if (isLowStock) LowStockRed else TextMuted,
                        maxLines = 1
                    )
                    if (isLowStock) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = LowStockRed
                        ) {
                            Text(
                                text = if (isBangla) "কম" else "Low",
                                style = AppTypographyScale.hintText.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Product Detail Bottom Sheet (Tap a product to view details, Edit, Delete, Add stock)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailBottomSheet(
    product: Product,
    currentLanguage: AppLanguage,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onAddStock: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkSlateSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Photo & Product Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        HardwareThumbnailIcon(
                            photoKey = product.photoKey,
                            category = product.category,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.localizedName(isBangla),
                        style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isBangla) product.nameEn else product.nameBn,
                        style = AppTypographyScale.hintText,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = product.category,
                        style = AppTypographyScale.hintText,
                        color = VividEmeraldAccent
                    )
                }
            }

            // Description if present
            if (product.description.isNotBlank()) {
                Text(
                    text = product.description,
                    style = AppTypographyScale.normalText,
                    color = TextMuted,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Stats Matrix: Stock Qty, Buy Price, Sell Price, Low Limit
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBangla) "মজুদ স্টক" else "In Stock",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        val qtyStr = if (isBangla) toBengaliNumerals(product.stockQuantity.toString()) else product.stockQuantity.toString()
                        Text(
                            text = "$qtyStr ${product.localizedUnit(isBangla)}",
                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                            color = if (product.isLowStock) LowStockRed else TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBangla) "ক্রয় মূল্য" else "Buy Price",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        Text(
                            text = formatTaka(product.buyPrice, currentLanguage),
                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBangla) "বিক্রয় মূল্য" else "Sell Price",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        Text(
                            text = formatTaka(product.sellPrice, currentLanguage),
                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                            color = VividEmeraldAccent
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBangla) "সতর্কতা সীমা" else "Low Limit",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        val limitStr = if (isBangla) toBengaliNumerals(product.lowStockThreshold.toString()) else product.lowStockThreshold.toString()
                        Text(
                            text = limitStr,
                            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                            color = TextMuted
                        )
                    }
                }
            }

            // Action Buttons: "Add stock", "Edit", "Delete"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // "Add stock" Button (Records a purchase)
                Button(
                    onClick = {
                        onDismiss()
                        onAddStock()
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("btn_sheet_add_stock"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBangla) "স্টক যোগ (ক্রয়)" else "Add Stock",
                            style = AppTypographyScale.buttonText
                        )
                    }
                }

                // "Edit" Button
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_sheet_edit_product"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSlateSurfaceVariant)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBangla) "সম্পাদনা" else "Edit",
                            style = AppTypographyScale.buttonText,
                            color = TextPrimary
                        )
                    }
                }

                // "Delete" Button (Soft delete)
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSlateSurfaceVariant)
                        .testTag("btn_sheet_delete_product")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = LowStockRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = DarkSlateSurface,
            title = {
                Text(
                    text = if (isBangla) "পণ্যটি ডিলিট করবেন?" else "Delete this product?",
                    style = AppTypographyScale.screenTitle,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = if (isBangla)
                        "পণ্যটি স্টক থেকে মুছে ফেলা হবে, তবে বিক্রির পূর্ববর্তী হিসাব সুরক্ষিত থাকবে।"
                    else
                        "This will remove the item from active stock. Past sales records will be kept safely.",
                    style = AppTypographyScale.normalText,
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LowStockRed),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = if (isBangla) "ডিলিট করুন" else "Delete",
                        style = AppTypographyScale.buttonText
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false },
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
}

/**
 * Add / Edit Product Dialog with all requested fields
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditProductDialog(
    title: String,
    categories: List<Category>,
    existingProduct: Product?,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        nameBn: String,
        nameEn: String,
        category: String,
        description: String,
        photoKey: String,
        stockQuantity: Int,
        buyPrice: Double,
        sellPrice: Double,
        lowStockThreshold: Int,
        unitBn: String,
        unitEn: String
    ) -> Unit
) {
    var nameBn by remember { mutableStateOf(existingProduct?.nameBn ?: "") }
    var nameEn by remember { mutableStateOf(existingProduct?.nameEn ?: "") }
    var selectedCat by remember { mutableStateOf(existingProduct?.category ?: (categories.firstOrNull()?.nameEn ?: "Hand Tools")) }
    var description by remember { mutableStateOf(existingProduct?.description ?: "") }
    var photoKey by remember { mutableStateOf(existingProduct?.photoKey ?: "hammer") }
    var stockQuantityText by remember { mutableStateOf(existingProduct?.stockQuantity?.toString() ?: "10") }
    var buyPriceText by remember { mutableStateOf(existingProduct?.buyPrice?.toLong()?.toString() ?: "100") }
    var sellPriceText by remember { mutableStateOf(existingProduct?.sellPrice?.toLong()?.toString() ?: "150") }
    var lowLimitText by remember { mutableStateOf(existingProduct?.lowStockThreshold?.toString() ?: "5") }
    var unitBn by remember { mutableStateOf(existingProduct?.unitBn ?: "টি") }
    var unitEn by remember { mutableStateOf(existingProduct?.unitEn ?: "pcs") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val photoOptions = listOf(
        "hammer" to "🔨 Hammer",
        "pvc_pipe" to "🚰 Pipe",
        "paint" to "🎨 Paint",
        "screwdriver" to "🪛 Screwdriver",
        "lock" to "🔒 Lock",
        "glasses" to "🥽 Glasses",
        "level" to "📏 Level",
        "switch" to "🔌 Switch",
        "nails" to "🔩 Nails",
        "wire" to "🧵 Wire",
        "light" to "💡 Light",
        "tools" to "⚙️ Tools"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = title,
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Name in Bangla
                OutlinedTextField(
                    value = nameBn,
                    onValueChange = { nameBn = it },
                    label = { Text(if (isBangla) "পণ্যের নাম (বাংলায়)" else "Product Name (Bangla)", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_product_name_bn"),
                    colors = dialogTextFieldColors()
                )

                // Name in English
                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text(if (isBangla) "পণ্যের নাম (ইংরেজিতে)" else "Product Name (English)", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_product_name_en"),
                    colors = dialogTextFieldColors()
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCat,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBangla) "ক্যাটাগরি" else "Category", style = AppTypographyScale.hintText) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("input_product_category"),
                        colors = dialogTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier.background(DarkSlateSurface)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.emoji} ${cat.localizedName(isBangla)}", color = TextPrimary, style = AppTypographyScale.normalText) },
                                onClick = {
                                    selectedCat = cat.nameEn
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Photo preset selector
                Text(
                    text = if (isBangla) "ফটোর আইকন নির্বাচন করুন:" else "Select Photo / Icon:",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    photoOptions.forEach { (key, label) ->
                        val isSelected = photoKey == key
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { photoKey = key },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) VividEmeraldPrimary else DarkSlateSurfaceVariant
                        ) {
                            Text(
                                text = label,
                                style = AppTypographyScale.hintText,
                                color = if (isSelected) Color.White else TextMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (isBangla) "বিবরণ" else "Description", style = AppTypographyScale.hintText) },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("input_product_description"),
                    colors = dialogTextFieldColors()
                )

                // Quantity & Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = stockQuantityText,
                        onValueChange = { stockQuantityText = it },
                        label = { Text(if (isBangla) "মজুদ পরিমাণ" else "Quantity", style = AppTypographyScale.hintText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1.2f).testTag("input_product_quantity"),
                        colors = dialogTextFieldColors()
                    )
                    OutlinedTextField(
                        value = if (isBangla) unitBn else unitEn,
                        onValueChange = {
                            if (isBangla) unitBn = it else unitEn = it
                        },
                        label = { Text(if (isBangla) "একক (টি/কেজি)" else "Unit", style = AppTypographyScale.hintText) },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f).testTag("input_product_unit"),
                        colors = dialogTextFieldColors()
                    )
                }

                // Buying Price & Selling Price (৳)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = buyPriceText,
                        onValueChange = { buyPriceText = it },
                        label = { Text(if (isBangla) "ক্রয় মূল্য (৳)" else "Buying Price (৳)", style = AppTypographyScale.hintText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_product_buy_price"),
                        colors = dialogTextFieldColors()
                    )
                    OutlinedTextField(
                        value = sellPriceText,
                        onValueChange = { sellPriceText = it },
                        label = { Text(if (isBangla) "বিক্রয় মূল্য (৳)" else "Selling Price (৳)", style = AppTypographyScale.hintText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_product_sell_price"),
                        colors = dialogTextFieldColors()
                    )
                }

                // Low Stock Threshold (default 5)
                OutlinedTextField(
                    value = lowLimitText,
                    onValueChange = { lowLimitText = it },
                    label = { Text(if (isBangla) "কম স্টক সতর্কতা সীমা (ডিফল্ট ৫)" else "Low Stock Limit (Default 5)", style = AppTypographyScale.hintText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_product_low_limit"),
                    colors = dialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = stockQuantityText.toIntOrNull() ?: 0
                    val buy = buyPriceText.toDoubleOrNull() ?: 0.0
                    val sell = sellPriceText.toDoubleOrNull() ?: 0.0
                    val limit = lowLimitText.toIntOrNull() ?: 5
                    onSave(nameBn, nameEn, selectedCat, description, photoKey, qty, buy, sell, limit, unitBn, unitEn)
                },
                modifier = Modifier.height(44.dp).testTag("btn_save_product"),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(
                    text = if (isBangla) "সংরক্ষণ করুন" else "Save Product",
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
private fun AddStockPurchaseDialog(
    product: Product,
    isBangla: Boolean,
    suppliers: List<Supplier>,
    onDismiss: () -> Unit,
    onConfirm: (
        addedQty: Int,
        unitPrice: Double,
        supplierId: Long?,
        supplierName: String,
        paidAmount: Double,
        dueAmount: Double,
        billPhotoUri: String?,
        note: String
    ) -> Unit
) {
    var addedQtyText by remember { mutableStateOf("10") }
    var buyingPriceText by remember { mutableStateOf(product.buyPrice.toLong().toString()) }
    var selectedSupplierId by remember { mutableStateOf<Long?>(suppliers.firstOrNull()?.id) }
    var customSupplierName by remember { mutableStateOf("") }
    var isNewSupplierMode by remember { mutableStateOf(suppliers.isEmpty()) }
    var paidAmountText by remember { mutableStateOf("") }
    var hasUserEditedPaid by remember { mutableStateOf(false) }
    var billPhotoUri by remember { mutableStateOf<String?>(null) }
    var note by remember { mutableStateOf("") }
    var isSupplierDropdownExpanded by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> billPhotoUri = uri?.toString() }
    )

    val qty = addedQtyText.toIntOrNull() ?: 0
    val unitPrice = buyingPriceText.toDoubleOrNull() ?: product.buyPrice
    val totalCost = qty * unitPrice

    // If user hasn't typed paid amount yet, default to totalCost
    val effectivePaidText = if (!hasUserEditedPaid) {
        if (totalCost > 0) totalCost.toLong().toString() else "0"
    } else {
        paidAmountText
    }
    val paidAmount = effectivePaidText.toDoubleOrNull() ?: 0.0
    val dueAmount = (totalCost - paidAmount).coerceAtLeast(0.0)

    val currentSelectedSupplier = suppliers.find { it.id == selectedSupplierId }
    val effectiveSupplierName = if (isNewSupplierMode) {
        customSupplierName
    } else {
        currentSelectedSupplier?.company?.ifBlank { currentSelectedSupplier.name } ?: ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "স্টক বৃদ্ধি করুন (ক্রয় রেকর্ড)" else "Add Stock (Record Purchase)",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${product.localizedName(isBangla)} (বর্তমান মজুদ: ${product.stockQuantity} ${product.localizedUnit(isBangla)})",
                    style = AppTypographyScale.normalText,
                    color = VividEmeraldAccent
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = addedQtyText,
                        onValueChange = { addedQtyText = it },
                        label = { Text(if (isBangla) "নতুন ক্রয়ের পরিমাণ" else "Added Qty", style = AppTypographyScale.hintText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_add_stock_qty"),
                        colors = dialogTextFieldColors()
                    )

                    OutlinedTextField(
                        value = buyingPriceText,
                        onValueChange = { buyingPriceText = it },
                        label = { Text(if (isBangla) "ক্রয়দর (৳)" else "Buying Price (৳)", style = AppTypographyScale.hintText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1.2f).testTag("input_add_stock_price"),
                        colors = dialogTextFieldColors()
                    )
                }

                // Total calculated cost banner
                Surface(
                    color = DarkSlateSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "মোট ক্রয়মূল্য:" else "Total Cost:",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                        Text(
                            text = if (isBangla) formatTaka(totalCost, AppLanguage.BANGLA) else formatTaka(totalCost, AppLanguage.ENGLISH),
                            style = AppTypographyScale.moneyNumber.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                            color = TextPrimary
                        )
                    }
                }

                // Supplier Selector
                Text(
                    text = if (isBangla) "মহাজন / সরবরাহকারী নির্বাচন করুন:" else "Select Supplier / Vendor:",
                    style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Medium),
                    color = TextPrimary
                )

                if (!isNewSupplierMode && suppliers.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = currentSelectedSupplier?.let { "${it.company.ifBlank { it.name }} (দেনা: ৳ ${it.amountOwed.toLong()})" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBangla) "মহাজন নির্বাচন" else "Select Supplier", style = AppTypographyScale.hintText) },
                            trailingIcon = {
                                TextButton(onClick = { isSupplierDropdownExpanded = true }) {
                                    Text(if (isBangla) "পরিবর্তন" else "Change", color = VividEmeraldAccent, style = AppTypographyScale.hintText)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSupplierDropdownExpanded = true }
                                .testTag("picker_select_supplier"),
                            colors = dialogTextFieldColors()
                        )

                        androidx.compose.material3.DropdownMenu(
                            expanded = isSupplierDropdownExpanded,
                            onDismissRequest = { isSupplierDropdownExpanded = false },
                            modifier = Modifier.background(DarkSlateSurface)
                        ) {
                            suppliers.forEach { s ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = s.company.ifBlank { s.name },
                                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${s.name} • ${if (isBangla) "দেনা:" else "Owed:"} ৳ ${s.amountOwed.toLong()}",
                                                style = AppTypographyScale.hintText,
                                                color = if (s.amountOwed > 0) LowStockRed else VividEmeraldAccent
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedSupplierId = s.id
                                        isSupplierDropdownExpanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isBangla) "+ অন্য নতুন মহাজন লিখুন" else "+ Type New Supplier",
                                        style = AppTypographyScale.normalText.copy(color = VividEmeraldAccent, fontWeight = FontWeight.Bold)
                                    )
                                },
                                onClick = {
                                    isNewSupplierMode = true
                                    isSupplierDropdownExpanded = false
                                }
                            )
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = customSupplierName,
                        onValueChange = { customSupplierName = it },
                        label = { Text(if (isBangla) "মহাজন / কোম্পানির নাম লিখুন" else "Supplier / Company Name", style = AppTypographyScale.hintText) },
                        trailingIcon = {
                            if (suppliers.isNotEmpty()) {
                                TextButton(onClick = { isNewSupplierMode = false }) {
                                    Text(if (isBangla) "তালিকা" else "List", color = VividEmeraldAccent, style = AppTypographyScale.hintText)
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_custom_supplier_name"),
                        colors = dialogTextFieldColors()
                    )
                }

                // "ENTER HOW MUCH WAS PAID NOW" (Explicit prompt requirement)
                OutlinedTextField(
                    value = effectivePaidText,
                    onValueChange = {
                        paidAmountText = it
                        hasUserEditedPaid = true
                    },
                    label = { Text(if (isBangla) "নগদ কত টাকা পরিশোধ করেছেন? (৳) *" else "How much was paid now? (৳) *", style = AppTypographyScale.hintText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_paid_now_amount"),
                    colors = dialogTextFieldColors()
                )

                // Quick payment buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = if (dueAmount == 0.0 && totalCost > 0) VividEmeraldPrimary else DarkSlateSurfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                paidAmountText = totalCost.toLong().toString()
                                hasUserEditedPaid = true
                            }
                    ) {
                        Text(
                            text = if (isBangla) "সম্পূর্ণ পরিশোধ" else "Full Paid",
                            style = AppTypographyScale.hintText.copy(
                                color = if (dueAmount == 0.0 && totalCost > 0) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Surface(
                        color = if (paidAmount == 0.0 && totalCost > 0) LowStockRed.copy(alpha = 0.8f) else DarkSlateSurfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                paidAmountText = "0"
                                hasUserEditedPaid = true
                            }
                    ) {
                        Text(
                            text = if (isBangla) "সম্পূর্ণ বাকি (০ ৳)" else "Zero Paid (Due)",
                            style = AppTypographyScale.hintText.copy(
                                color = if (paidAmount == 0.0 && totalCost > 0) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // UNPAID / DUE ALERT (Explicit prompt requirement: Any unpaid part is added to what the shop owes that supplier)
                if (dueAmount > 0) {
                    Surface(
                        color = LowStockRedContainer,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, LowStockRed.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isBangla)
                                    "বাকি দেনা: ${formatTaka(dueAmount, AppLanguage.BANGLA)} (মহাজনের দেনায় যুক্ত হবে)"
                                else
                                    "Unpaid Due: ${formatTaka(dueAmount, AppLanguage.ENGLISH)} (will be added to what shop owes supplier)",
                                style = AppTypographyScale.hintText.copy(color = LowStockRed, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                // "ATTACH A PHOTO OF THE BILL" (Explicit prompt requirement)
                Text(
                    text = if (isBangla) "চালান বা বিলের ছবি (ঐচ্ছিক):" else "Bill / Invoice Photo (Optional):",
                    style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Medium),
                    color = TextPrimary
                )

                if (billPhotoUri != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurfaceVariant),
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AsyncImage(
                                    model = billPhotoUri,
                                    contentDescription = "Bill Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                                Text(
                                    text = if (isBangla) "বিলের ছবি সংযুক্ত" else "Bill photo attached",
                                    style = AppTypographyScale.hintText.copy(color = VividEmeraldAccent, fontWeight = FontWeight.Bold)
                                )
                            }

                            IconButton(onClick = { billPhotoUri = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove Photo", tint = LowStockRed)
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("btn_attach_bill_photo"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, DarkSlateCardBorder)
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = VividEmeraldAccent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "বিলের ছবি সংযুক্ত করুন" else "Attach Photo of Bill",
                            style = AppTypographyScale.buttonText.copy(fontSize = 13.sp)
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (isBangla) "নোট / চালান নম্বর" else "Note / Invoice Number", style = AppTypographyScale.hintText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_add_stock_note"),
                    colors = dialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (qty > 0) {
                        val supplierId = if (!isNewSupplierMode) selectedSupplierId else null
                        onConfirm(qty, unitPrice, supplierId, effectiveSupplierName, paidAmount, dueAmount, billPhotoUri, note)
                    }
                },
                modifier = Modifier.height(44.dp).testTag("btn_confirm_add_stock"),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(
                    text = if (isBangla) "স্টক যুক্ত করুন" else "Confirm Stock",
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
 * Category Manager Sheet: User can add, rename, hide, and reorder categories later
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryManagerSheet(
    categories: List<Category>,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onAddCategory: (nameEn: String, nameBn: String, emoji: String) -> Unit,
    onUpdateCategory: (Category) -> Unit,
    onToggleVisibility: (Category) -> Unit,
    onDeleteCategory: (Category) -> Unit
) {
    var newEnName by remember { mutableStateOf("") }
    var newBnName by remember { mutableStateOf("") }
    var newEmoji by remember { mutableStateOf("📦") }
    var categoryToRename by remember { mutableStateOf<Category?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkSlateSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isBangla) "ক্যাটাগরি ব্যবস্থাপনা" else "Manage Categories",
                style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = if (isBangla)
                    "এখানে আপনি নতুন ক্যাটাগরি যোগ করতে পারেন, নাম পরিবর্তন করতে পারেন এবং প্রদর্শন বন্ধ/চালু করতে পারেন।"
                else
                    "Add, rename, hide, and reorder your hardware categories anytime.",
                style = AppTypographyScale.hintText,
                color = TextMuted
            )

            // Category List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSlateSurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = cat.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = cat.localizedName(isBangla),
                                        style = AppTypographyScale.normalText,
                                        color = if (cat.isVisible) TextPrimary else TextMuted
                                    )
                                    Text(
                                        text = "${cat.nameEn} • ${cat.nameBn}",
                                        style = AppTypographyScale.hintText,
                                        color = TextMuted
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Toggle visibility (hide/show)
                                IconButton(
                                    onClick = { onToggleVisibility(cat) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (cat.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Visibility",
                                        tint = if (cat.isVisible) VividEmeraldAccent else TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Rename / Edit
                                IconButton(
                                    onClick = { categoryToRename = cat },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Rename",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Add New Category Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateBackground),
                border = BorderStroke(1.dp, DarkSlateCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBangla) "নতুন ক্যাটাগরি তৈরি করুন" else "Create New Category",
                        style = AppTypographyScale.sectionHeading,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newEmoji,
                            onValueChange = { newEmoji = it },
                            label = { Text("ইমোজি", style = AppTypographyScale.hintText) },
                            singleLine = true,
                            modifier = Modifier.width(68.dp),
                            colors = dialogTextFieldColors()
                        )
                        OutlinedTextField(
                            value = newEnName,
                            onValueChange = { newEnName = it },
                            label = { Text("English Name", style = AppTypographyScale.hintText) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = dialogTextFieldColors()
                        )
                    }

                    OutlinedTextField(
                        value = newBnName,
                        onValueChange = { newBnName = it },
                        label = { Text("বাংলা নাম", style = AppTypographyScale.hintText) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = dialogTextFieldColors()
                    )

                    Button(
                        onClick = {
                            if (newEnName.isNotBlank()) {
                                onAddCategory(newEnName, newBnName, newEmoji)
                                newEnName = ""
                                newBnName = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
                    ) {
                        Text(
                            text = if (isBangla) "ক্যাটাগরি যোগ করুন" else "Add Category",
                            style = AppTypographyScale.buttonText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Rename Dialog
    categoryToRename?.let { cat ->
        var renameEn by remember { mutableStateOf(cat.nameEn) }
        var renameBn by remember { mutableStateOf(cat.nameBn) }
        var renameEmoji by remember { mutableStateOf(cat.emoji) }

        AlertDialog(
            onDismissRequest = { categoryToRename = null },
            containerColor = DarkSlateSurface,
            title = {
                Text(
                    text = if (isBangla) "ক্যাটাগরি সম্পাদনা" else "Edit Category",
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
                        value = renameEmoji,
                        onValueChange = { renameEmoji = it },
                        label = { Text("Emoji", style = AppTypographyScale.hintText) },
                        singleLine = true,
                        colors = dialogTextFieldColors()
                    )
                    OutlinedTextField(
                        value = renameEn,
                        onValueChange = { renameEn = it },
                        label = { Text("English Name", style = AppTypographyScale.hintText) },
                        singleLine = true,
                        colors = dialogTextFieldColors()
                    )
                    OutlinedTextField(
                        value = renameBn,
                        onValueChange = { renameBn = it },
                        label = { Text("বাংলা নাম", style = AppTypographyScale.hintText) },
                        singleLine = true,
                        colors = dialogTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateCategory(cat.copy(nameEn = renameEn, nameBn = renameBn, emoji = renameEmoji))
                        categoryToRename = null
                    },
                    modifier = Modifier.height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
                ) {
                    Text(
                        text = if (isBangla) "আপডেট করুন" else "Update",
                        style = AppTypographyScale.buttonText
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { categoryToRename = null },
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
}

/**
 * Empty View when no products match filter or search
 */
@Composable
private fun EmptyProductsView(isBangla: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Inventory2,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isBangla) "কোনো পণ্য পাওয়া যায়নি" else "No products found",
            style = AppTypographyScale.sectionHeading,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isBangla)
                "অনুসন্ধান পরিবর্তন করুন অথবা নিচের '+' বাটনে ট্যাপ করে নতুন পণ্য যোগ করুন।"
            else
                "Try another search or tap the '+' button below to add a new product.",
            style = AppTypographyScale.hintText,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Helper to select realistic hardware icon based on photoKey and category
 */
@Composable
private fun HardwareThumbnailIcon(
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
        "level" -> Icons.Default.Straighten
        "switch" -> Icons.Default.Power
        "nails" -> Icons.Default.Construction
        "wire" -> Icons.Default.Cable
        "light" -> Icons.Default.Lightbulb
        else -> when (category.lowercase()) {
            "paint & chemicals" -> Icons.Default.FormatPaint
            "pipes & fittings", "sanitary & bathroom" -> Icons.Default.Plumbing
            "electrical fittings", "fans & appliances" -> Icons.Default.Power
            "lights & bulbs" -> Icons.Default.Lightbulb
            "wire & cable" -> Icons.Default.Cable
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

@Composable
private fun dialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = VividEmeraldPrimary,
    unfocusedBorderColor = DarkSlateCardBorder,
    focusedContainerColor = DarkSlateSurfaceVariant,
    unfocusedContainerColor = DarkSlateSurfaceVariant,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = VividEmeraldAccent,
    unfocusedLabelColor = TextMuted
)
