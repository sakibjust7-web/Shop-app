package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.backup.BackupSummary
import com.example.data.local.AppSettings
import com.example.data.local.ViewMode
import com.example.data.local.entity.Category
import com.example.ui.localization.AppLanguage
import com.example.ui.screens.settings.SettingsViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AppTypographyScale
import com.example.ui.theme.DarkSlateBackground
import com.example.ui.theme.DarkSlateCardBorder
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VividEmeraldAccent
import com.example.ui.theme.VividEmeraldPrimary
import com.example.ui.theme.WarmAmber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Curated hardware tool emoji set for category icons
val HARDWARE_EMOJI_PALETTE = listOf(
    "🔨", "🪚", "🔩", "🔌", "🧵", "💡", "🚰", "🚿",
    "🎨", "🧴", "🔒", "🧱", "⚙️", "🥽", "🌀", "📦",
    "🧰", "🪛", "📐", "🪜", "🧯", "🚜", "🏠", "⚡"
)

// Curated modern color palette for category chips
val CATEGORY_COLOR_PALETTE = listOf(
    "#10B981" to "Emerald",
    "#3B82F6" to "Blue",
    "#06B6D4" to "Cyan",
    "#8B5CF6" to "Purple",
    "#F59E0B" to "Amber",
    "#F97316" to "Orange",
    "#EC4899" to "Pink",
    "#EF4444" to "Red",
    "#14B8A6" to "Teal",
    "#64748B" to "Slate"
)

fun parseHexColor(hex: String, fallback: Color = Color(0xFF10B981)): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) {
            Color(colorInt or 0x00000000FF000000)
        } else {
            Color(colorInt)
        }
    } catch (e: Exception) {
        fallback
    }
}

@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val isBangla = currentLanguage == AppLanguage.BANGLA
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val localBackups by viewModel.localBackups.collectAsStateWithLifecycle()
    val isBusy by viewModel.isBusy.collectAsStateWithLifecycle()
    val statusMsg by viewModel.statusMessage.collectAsStateWithLifecycle()
    val pendingSummary by viewModel.pendingRestoreSummary.collectAsStateWithLifecycle()

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    // Toast listener
    LaunchedEffect(statusMsg) {
        statusMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearStatusMessage()
        }
    }

    // Android Storage Access Framework for Save Backup
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.saveBackupToPhoneFile(uri) { success, msg ->
                val displayMsg = if (success) {
                    if (isBangla) "ফোনে ব্যাকআপ সফলভাবে সংরক্ষিত হয়েছে!" else "Backup saved to phone successfully!"
                } else {
                    msg
                }
                Toast.makeText(context, displayMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Android SAF for Restore
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.prepareRestoreFromUri(uri) { errMsg ->
                Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    BackHandler {
        onBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSlateBackground)
            .testTag("settings_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header card
            item {
                SettingsHeaderCard(isBangla = isBangla)
            }

            // Section 1: Language
            item {
                SettingsSectionHeader(
                    icon = Icons.Default.Language,
                    title = if (isBangla) "ভাষা নির্বাচন (Language)" else "App Language"
                )
            }

            item {
                LanguageSelectionCard(
                    currentLanguage = currentLanguage,
                    onSelectLanguage = { lang ->
                        viewModel.setLanguage(lang)
                    }
                )
            }

            // Section 2: View Mode (Simple vs Detailed)
            item {
                SettingsSectionHeader(
                    icon = Icons.Default.GridView,
                    title = if (isBangla) "স্ক্রিন ভিউ মোড (Simple / Detailed)" else "Screen View Mode"
                )
            }

            item {
                ViewModeSelectionCard(
                    isBangla = isBangla,
                    currentViewMode = AppSettings.viewMode,
                    onSelectViewMode = { mode ->
                        viewModel.setViewMode(mode)
                    }
                )
            }

            // Section 3: Manage Categories
            item {
                SettingsSectionHeader(
                    icon = Icons.Default.Category,
                    title = if (isBangla) "ক্যাটাগরি পরিচালনা (${allCategories.size}টি)" else "Manage Categories (${allCategories.size})"
                )
            }

            item {
                CategoryManagementHeader(
                    isBangla = isBangla,
                    onAddClick = { showAddCategoryDialog = true }
                )
            }

            itemsIndexed(allCategories, key = { _, cat -> cat.id }) { index, cat ->
                CategoryManagementRow(
                    category = cat,
                    isBangla = isBangla,
                    canMoveUp = index > 0,
                    canMoveDown = index < allCategories.size - 1,
                    onMoveUp = { viewModel.moveCategory(cat, true) },
                    onMoveDown = { viewModel.moveCategory(cat, false) },
                    onToggleVisibility = { viewModel.toggleCategoryVisibility(cat) },
                    onEdit = { categoryToEdit = cat },
                    onDelete = { categoryToDelete = cat }
                )
            }

            // Section 4: Data Backup & Restore
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SettingsSectionHeader(
                    icon = Icons.Default.Backup,
                    title = if (isBangla) "ডাটা ব্যাকআপ ও রিস্টোর (Backup & Restore)" else "Data Backup & Restore"
                )
            }

            item {
                BackupActionsCard(
                    isBangla = isBangla,
                    onSaveToPhone = {
                        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        createDocumentLauncher.launch("HardwareManager_Backup_$timestamp.json")
                    },
                    onGoogleDriveShare = {
                        viewModel.shareToGoogleDrive { shareIntent ->
                            context.startActivity(shareIntent)
                        }
                    },
                    onRestoreFromFile = {
                        openDocumentLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                    }
                )
            }

            // Local Backups List
            if (localBackups.isNotEmpty()) {
                item {
                    Text(
                        text = if (isBangla) "সাম্প্রতিক লোকাল ব্যাকআপ (${localBackups.size}টি)" else "Recent Local Backups (${localBackups.size})",
                        style = AppTypographyScale.sectionHeading,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                itemsIndexed(localBackups) { _, file ->
                    LocalBackupItemCard(
                        file = file,
                        isBangla = isBangla,
                        onRestore = {
                            viewModel.prepareRestoreFromFile(file) { errMsg ->
                                Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
                            }
                        },
                        onShare = {
                            viewModel.shareToGoogleDrive { shareIntent ->
                                context.startActivity(shareIntent)
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Loading overlay if busy
        if (isBusy) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, VividEmeraldAccent)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = VividEmeraldAccent)
                        Text(
                            text = if (isBangla) "কাজ চলছে, অনুগ্রহ করে অপেক্ষা করুন..." else "Processing, please wait...",
                            style = AppTypographyScale.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        CategoryEditDialog(
            isBangla = isBangla,
            initialCategory = null,
            onDismiss = { showAddCategoryDialog = false },
            onSave = { nameEn, nameBn, emoji, colorHex ->
                viewModel.addCategory(nameEn, nameBn, emoji, colorHex)
                showAddCategoryDialog = false
            }
        )
    }

    // Edit Category Dialog
    categoryToEdit?.let { cat ->
        CategoryEditDialog(
            isBangla = isBangla,
            initialCategory = cat,
            onDismiss = { categoryToEdit = null },
            onSave = { nameEn, nameBn, emoji, colorHex ->
                viewModel.updateCategory(
                    cat.copy(
                        nameEn = nameEn,
                        nameBn = nameBn,
                        emoji = emoji,
                        colorHex = colorHex
                    )
                )
                categoryToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    categoryToDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            containerColor = DarkSlateSurface,
            title = {
                Text(
                    text = if (isBangla) "ক্যাটাগরি মুছে ফেলা নিশ্চিত করুন" else "Delete Category",
                    style = AppTypographyScale.screenTitle,
                    color = AlertRed
                )
            },
            text = {
                Text(
                    text = if (isBangla)
                        "আপনি কি নিশ্চিত যে '${cat.localizedName(true)}' ক্যাটাগরিটি মুছে ফেলতে চান?"
                    else
                        "Are you sure you want to delete '${cat.nameEn}' category?",
                    style = AppTypographyScale.bodySmall,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(cat)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    modifier = Modifier.testTag("confirm_delete_category_button")
                ) {
                    Text(if (isBangla) "মুছে ফেলুন" else "Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
                }
            }
        )
    }

    // Restore Confirmation Dialog (MANDATORY REQUIREMENT)
    pendingSummary?.let { summary ->
        RestoreConfirmationDialog(
            summary = summary,
            isBangla = isBangla,
            onDismiss = { viewModel.dismissRestoreConfirmation() },
            onConfirm = {
                viewModel.confirmRestore { result ->
                    if (result.success) {
                        Toast.makeText(
                            context,
                            if (isBangla) "রিস্টোর সফল হয়েছে! সকল ডাটা প্রতিস্থাপিত হয়েছে।" else "Restore successful! All data replaced.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

@Composable
private fun SettingsHeaderCard(isBangla: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        imageVector = Icons.Default.Backup,
                        contentDescription = null,
                        tint = VividEmeraldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isBangla) "দোকানের সেটিংস ও পরিচালনা" else "Store Settings & Controls",
                        style = AppTypographyScale.screenTitle,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isBangla)
                            "ভাষা, ভিউ মোড, ক্যাটাগরি ও অফলাইন ব্যাকআপ ম্যানেজমেন্ট"
                        else
                            "Language, view modes, categories & offline backup control",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VividEmeraldAccent,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = AppTypographyScale.sectionHeading,
            color = TextPrimary
        )
    }
}

@Composable
private fun LanguageSelectionCard(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AppLanguage.values().forEach { lang ->
                val isSelected = currentLanguage == lang
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectLanguage(lang) }
                        .testTag("settings_lang_${lang.code}"),
                    color = if (isSelected) DarkSlateSurfaceVariant else Color.Transparent,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) VividEmeraldAccent else DarkSlateCardBorder.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) VividEmeraldPrimary.copy(alpha = 0.2f) else DarkSlateBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.BANGLA) "বাং" else "EN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) VividEmeraldAccent else TextMuted
                                )
                            }
                            Column {
                                Text(
                                    text = lang.nativeName,
                                    style = AppTypographyScale.cardTitle,
                                    color = if (isSelected) VividEmeraldAccent else TextPrimary
                                )
                                Text(
                                    text = lang.displayName,
                                    style = AppTypographyScale.hintText,
                                    color = TextMuted
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isSelected) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isSelected) VividEmeraldAccent else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewModeSelectionCard(
    isBangla: Boolean,
    currentViewMode: ViewMode,
    onSelectViewMode: (ViewMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ViewMode.values().forEach { mode ->
                val isSelected = currentViewMode == mode
                val icon = if (mode == ViewMode.SIMPLE) Icons.Default.GridView else Icons.Default.ViewAgenda

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectViewMode(mode) }
                        .testTag("settings_view_mode_${mode.name.lowercase()}"),
                    color = if (isSelected) DarkSlateSurfaceVariant else Color.Transparent,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) VividEmeraldAccent else DarkSlateCardBorder.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) VividEmeraldPrimary.copy(alpha = 0.25f) else DarkSlateBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) VividEmeraldAccent else TextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBangla) mode.titleBn else mode.titleEn,
                                style = AppTypographyScale.cardTitle,
                                color = if (isSelected) VividEmeraldAccent else TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBangla) mode.descBn else mode.descEn,
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                        }

                        Icon(
                            imageVector = if (isSelected) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isSelected) VividEmeraldAccent else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryManagementHeader(
    isBangla: Boolean,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isBangla) "নাম, রঙ, আইকন পরিবর্তন ও সাজান" else "Rename, reorder, color & hide",
            style = AppTypographyScale.hintText,
            color = TextMuted
        )

        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("add_category_button")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isBangla) "নতুন ক্যাটাগরি" else "Add Category",
                style = AppTypographyScale.buttonText
            )
        }
    }
}

@Composable
private fun CategoryManagementRow(
    category: Category,
    isBangla: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleVisibility: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = parseHexColor(category.colorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("category_row_${category.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, if (category.isVisible) DarkSlateCardBorder else DarkSlateCardBorder.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Reorder controls
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = canMoveUp,
                    modifier = Modifier.size(32.dp).testTag("category_up_${category.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Move Up",
                        tint = if (canMoveUp) TextPrimary else TextMuted.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = canMoveDown,
                    modifier = Modifier.size(32.dp).testTag("category_down_${category.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Move Down",
                        tint = if (canMoveDown) TextPrimary else TextMuted.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Icon with customized color background
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.2f))
                    .border(2.dp, categoryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.emoji.ifBlank { "📦" },
                    fontSize = 20.sp
                )
            }

            // Names
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = category.localizedName(isBangla),
                        style = AppTypographyScale.cardTitle,
                        color = if (category.isVisible) TextPrimary else TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!category.isVisible) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarkSlateSurfaceVariant
                        ) {
                            Text(
                                text = if (isBangla) "লুকানো" else "Hidden",
                                style = AppTypographyScale.hintText.copy(fontSize = 10.sp),
                                color = TextMuted,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = if (isBangla) category.nameEn else category.nameBn,
                    style = AppTypographyScale.hintText,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Visibility toggle button
            IconButton(
                onClick = onToggleVisibility,
                modifier = Modifier.size(36.dp).testTag("category_visibility_${category.id}")
            ) {
                Icon(
                    imageVector = if (category.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Visibility",
                    tint = if (category.isVisible) VividEmeraldAccent else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Edit button
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp).testTag("category_edit_${category.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp).testTag("category_delete_${category.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = AlertRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryEditDialog(
    isBangla: Boolean,
    initialCategory: Category?,
    onDismiss: () -> Unit,
    onSave: (nameEn: String, nameBn: String, emoji: String, colorHex: String) -> Unit
) {
    var nameEn by remember { mutableStateOf(initialCategory?.nameEn ?: "") }
    var nameBn by remember { mutableStateOf(initialCategory?.nameBn ?: "") }
    var selectedEmoji by remember { mutableStateOf(initialCategory?.emoji ?: "🔨") }
    var selectedColorHex by remember { mutableStateOf(initialCategory?.colorHex ?: "#10B981") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (initialCategory == null) {
                    if (isBangla) "নতুন ক্যাটাগরি তৈরি" else "Create Category"
                } else {
                    if (isBangla) "ক্যাটাগরি সম্পাদনা" else "Edit Category"
                },
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Preview chip
                    val previewColor = parseHexColor(selectedColorHex)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSlateSurfaceVariant)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(previewColor.copy(alpha = 0.2f))
                                .border(2.dp, previewColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = selectedEmoji, fontSize = 24.sp)
                        }
                        Column {
                            Text(
                                text = nameBn.ifBlank { if (isBangla) "ক্যাটাগরি নাম" else "Bangla Name" },
                                style = AppTypographyScale.cardTitle,
                                color = TextPrimary
                            )
                            Text(
                                text = nameEn.ifBlank { "English Name" },
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = nameEn,
                        onValueChange = { nameEn = it },
                        label = { Text("English Name", style = AppTypographyScale.hintText) },
                        singleLine = true,
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("category_input_en")
                    )
                }

                item {
                    OutlinedTextField(
                        value = nameBn,
                        onValueChange = { nameBn = it },
                        label = { Text("বাংলা নাম (Bangla Name)", style = AppTypographyScale.hintText) },
                        singleLine = true,
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("category_input_bn")
                    )
                }

                // Choose Icon / Emoji
                item {
                    Text(
                        text = if (isBangla) "আইকন নির্বাচন করুন:" else "Choose Icon:",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HARDWARE_EMOJI_PALETTE.forEach { emoji ->
                            val isSelected = selectedEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) VividEmeraldPrimary.copy(alpha = 0.3f) else DarkSlateSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) VividEmeraldAccent else DarkSlateCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }

                // Choose Color
                item {
                    Text(
                        text = if (isBangla) "রঙ নির্বাচন করুন:" else "Choose Color:",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CATEGORY_COLOR_PALETTE.forEach { (hex, name) ->
                            val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                            val color = parseHexColor(hex)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (isSelected) 3.dp else 1.dp,
                                        if (isSelected) Color.White else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { selectedColorHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = name,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameEn.isNotBlank() || nameBn.isNotBlank()) {
                        onSave(nameEn.ifBlank { nameBn }, nameBn.ifBlank { nameEn }, selectedEmoji, selectedColorHex)
                    }
                },
                enabled = nameEn.isNotBlank() || nameBn.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                modifier = Modifier.testTag("save_category_dialog_button")
            ) {
                Text(if (isBangla) "সংরক্ষণ করুন" else "Save", color = TextPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
private fun BackupActionsCard(
    isBangla: Boolean,
    onSaveToPhone: () -> Unit,
    onGoogleDriveShare: () -> Unit,
    onRestoreFromFile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VividEmeraldPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Backup,
                        contentDescription = null,
                        tint = VividEmeraldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isBangla) "পূর্ণাঙ্গ ডাটা সুরক্ষা" else "Complete Store Data Backup",
                        style = AppTypographyScale.cardTitle,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isBangla)
                            "পণ্য, বিক্রি, খরচের হিসাব, কাস্টমার বাকি ও সরবরাহকারী তালিকা এক ফাইলে।"
                        else
                            "Products, sales, expenses, customers, suppliers & categories in one file.",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                }
            }

            HorizontalDivider(color = DarkSlateCardBorder.copy(alpha = 0.5f))

            // Action 1: Save Backup to Phone
            Button(
                onClick = onSaveToPhone,
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_backup_phone_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "ফোনে ফাইল হিসেবে সেভ করুন (Save to Phone)" else "Save Backup to Phone",
                    style = AppTypographyScale.buttonText
                )
            }

            // Action 2: Back up to Google Drive
            OutlinedButton(
                onClick = onGoogleDriveShare,
                border = BorderStroke(1.dp, VividEmeraldAccent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VividEmeraldAccent),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("backup_google_drive_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "গুগল ড্রাইভে ব্যাকআপ / শেয়ার করুন (Drive Backup)" else "Back up to Google Drive / Share",
                    style = AppTypographyScale.buttonText
                )
            }

            // Action 3: Restore from File
            OutlinedButton(
                onClick = onRestoreFromFile,
                border = BorderStroke(1.dp, WarmAmber),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmAmber),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("restore_from_file_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = null,
                    tint = WarmAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "ফাইল থেকে রিস্টোর করুন (Restore from File)" else "Restore from Backup File",
                    style = AppTypographyScale.buttonText,
                    color = WarmAmber
                )
            }

            // Warning note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isBangla)
                        "রিস্টোর করার পূর্বে বর্তমান ডাটা প্রতিস্থাপনের সতর্কতা নিশ্চিত করতে হবে।"
                    else
                        "Restoring always requires confirmation as it replaces current data.",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun LocalBackupItemCard(
    file: File,
    isBangla: Boolean,
    onRestore: () -> Unit,
    onShare: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(file.lastModified()))
    val sizeKb = (file.length() / 1024.0)
    val formattedSize = String.format(Locale.US, "%.1f KB", sizeKb)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = BorderStroke(1.dp, DarkSlateCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = AppTypographyScale.cardTitle.copy(fontSize = 13.sp),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = dateStr,
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                    Text(
                        text = "• $formattedSize",
                        style = AppTypographyScale.hintText,
                        color = VividEmeraldAccent
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Button(
                    onClick = onRestore,
                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (isBangla) "রিস্টোর" else "Restore",
                        style = AppTypographyScale.buttonText.copy(fontSize = 12.sp),
                        color = WarmAmber
                    )
                }
            }
        }
    }
}

/**
 * MANDATORY REQUIREMENT: Confirmation Dialog before restoring data
 */
@Composable
private fun RestoreConfirmationDialog(
    summary: BackupSummary,
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
                contentDescription = "Warning",
                tint = AlertRed,
                modifier = Modifier.size(40.dp)
            )
        },
        title = {
            Text(
                text = if (isBangla) "⚠️ বর্তমান ডাটা প্রতিস্থাপিত হবে!" else "⚠️ Overwrite Current Data?",
                style = AppTypographyScale.screenTitle,
                color = AlertRed,
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
                        "সতর্কতা: ব্যাকআপ ফাইলটি রিস্টোর করলে বর্তমান দোকানের সমস্ত পণ্য, বিক্রি, খরচের হিসাব এবং কাস্টমার তালিকা মুছে এই ফাইলের তথ্য দিয়ে সম্পূর্ণরূপে প্রতিস্থাপিত হবে।"
                    else
                        "Warning: Restoring this file will completely REPLACE and overwrite all current products, sales, expenses, and customer records with the backup file.",
                    style = AppTypographyScale.bodySmall,
                    color = TextPrimary
                )

                // Summary Breakdown Card
                Surface(
                    color = DarkSlateBackground,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, DarkSlateCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isBangla) "ফাইলের তথ্য বিবরণী:" else "Backup File Summary:",
                            style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                            color = VividEmeraldAccent
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "ব্যাকআপ তৈরির তারিখ:" else "Export Date:", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(text = summary.exportDateFormatted, style = AppTypographyScale.hintText, color = TextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "পণ্য সংখ্যা:" else "Products:", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(text = "${summary.productsCount} টি", style = AppTypographyScale.hintText, color = TextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "বিক্রির চালান:" else "Sales Invoices:", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(text = "${summary.salesCount} টি", style = AppTypographyScale.hintText, color = TextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "কাস্টমার হিসাব:" else "Customers:", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(text = "${summary.customersCount} জন", style = AppTypographyScale.hintText, color = TextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "সরবরাহকারী:" else "Suppliers:", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(text = "${summary.suppliersCount} জন", style = AppTypographyScale.hintText, color = TextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "ক্যাটাগরি:" else "Categories:", style = AppTypographyScale.hintText, color = TextMuted)
                            Text(text = "${summary.categoriesCount} টি", style = AppTypographyScale.hintText, color = TextPrimary)
                        }
                    }
                }

                Text(
                    text = if (isBangla)
                        "আপনি কি নিশ্চিত যে বর্তমান ডাটা প্রতিস্থাপন করে এগিয়ে যেতে চান?"
                    else
                        "Are you sure you want to proceed and replace all current store data?",
                    style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.SemiBold),
                    color = WarmAmber
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                modifier = Modifier.testTag("confirm_restore_button")
            ) {
                Text(
                    text = if (isBangla) "হ্যাঁ, প্রতিস্থাপন করুন" else "Yes, Replace All Data",
                    style = AppTypographyScale.buttonText,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, DarkSlateCardBorder),
                modifier = Modifier.testTag("cancel_restore_button")
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
