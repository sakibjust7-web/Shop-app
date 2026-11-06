package com.example.ui.screens.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.backup.BackupManager
import com.example.data.backup.BackupSummary
import com.example.data.backup.RestoreResult
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettings
import com.example.data.local.ViewMode
import com.example.data.local.entity.Category
import com.example.data.repository.HardwareRepository
import com.example.ui.localization.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = HardwareRepository(
        productDao = db.productDao(),
        saleOrderDao = db.saleOrderDao(),
        categoryDao = db.categoryDao(),
        purchaseDao = db.purchaseDao(),
        customerDao = db.customerDao(),
        supplierDao = db.supplierDao(),
        supplierPaymentDao = db.supplierPaymentDao(),
        customerPaymentDao = db.customerPaymentDao(),
        expenseDao = db.expenseDao()
    )

    private val backupManager = BackupManager(repository)

    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _localBackups = MutableStateFlow<List<File>>(emptyList())
    val localBackups: StateFlow<List<File>> = _localBackups.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Confirmation dialog state for restore
    private val _pendingRestoreSummary = MutableStateFlow<BackupSummary?>(null)
    val pendingRestoreSummary: StateFlow<BackupSummary?> = _pendingRestoreSummary.asStateFlow()

    private var pendingRestoreContent: String? = null

    init {
        refreshLocalBackups()
    }

    fun setLanguage(language: AppLanguage) {
        AppSettings.setLanguage(getApplication(), language)
    }

    fun setViewMode(mode: ViewMode) {
        AppSettings.setViewMode(mode, getApplication())
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // Category Management
    fun addCategory(nameEn: String, nameBn: String, emoji: String, colorHex: String) {
        viewModelScope.launch {
            val count = allCategories.value.size
            val category = Category(
                nameEn = nameEn.trim(),
                nameBn = nameBn.trim().ifBlank { nameEn.trim() },
                emoji = emoji.trim().ifBlank { "📦" },
                sortOrder = count + 1,
                isVisible = true,
                colorHex = colorHex.ifBlank { "#10B981" }
            )
            repository.insertCategory(category)
            _statusMessage.value = "Category added successfully"
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
            _statusMessage.value = "Category updated"
        }
    }

    fun toggleCategoryVisibility(category: Category) {
        viewModelScope.launch {
            repository.setCategoryVisibility(category, !category.isVisible)
        }
    }

    fun moveCategory(category: Category, moveUp: Boolean) {
        viewModelScope.launch {
            repository.moveCategory(category, moveUp)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            _statusMessage.value = "Category deleted"
        }
    }

    // Backup & Restore
    fun refreshLocalBackups() {
        viewModelScope.launch {
            _localBackups.value = backupManager.listLocalBackups(getApplication())
        }
    }

    suspend fun prepareBackupJson(): String {
        return backupManager.generateBackupJson(getApplication())
    }

    fun saveBackupToPhoneFile(uri: Uri, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isBusy.value = true
            try {
                val json = backupManager.generateBackupJson(getApplication())
                val context = getApplication<Application>()
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    os.write(json.toByteArray(Charsets.UTF_8))
                }
                // Also save to app internal backup list
                backupManager.saveBackupToLocalFile(context, json)
                refreshLocalBackups()
                _isBusy.value = false
                onComplete(true, "Backup saved successfully!")
            } catch (e: Exception) {
                _isBusy.value = false
                onComplete(false, "Failed to save: ${e.localizedMessage}")
            }
        }
    }

    fun shareToGoogleDrive(onIntentReady: (Intent) -> Unit) {
        viewModelScope.launch {
            _isBusy.value = true
            try {
                val json = backupManager.generateBackupJson(getApplication())
                val shareIntent = backupManager.createGoogleDriveShareIntent(getApplication(), json)
                // Save a local copy too
                backupManager.saveBackupToLocalFile(getApplication(), json)
                refreshLocalBackups()
                _isBusy.value = false
                onIntentReady(shareIntent)
            } catch (e: Exception) {
                _isBusy.value = false
                _statusMessage.value = "Error creating backup for Google Drive: ${e.localizedMessage}"
            }
        }
    }

    fun prepareRestoreFromUri(uri: Uri, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isBusy.value = true
            try {
                val context = getApplication<Application>()
                val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader(Charsets.UTF_8).readText()
                } ?: throw IllegalArgumentException("Could not read backup file")

                val summary = backupManager.inspectBackupJson(json)
                pendingRestoreContent = json
                _pendingRestoreSummary.value = summary
                _isBusy.value = false
            } catch (e: Exception) {
                _isBusy.value = false
                onError("Invalid backup file: ${e.localizedMessage}")
            }
        }
    }

    fun prepareRestoreFromFile(file: File, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isBusy.value = true
            try {
                val json = file.readText(Charsets.UTF_8)
                val summary = backupManager.inspectBackupJson(json)
                pendingRestoreContent = json
                _pendingRestoreSummary.value = summary
                _isBusy.value = false
            } catch (e: Exception) {
                _isBusy.value = false
                onError("Invalid backup file: ${e.localizedMessage}")
            }
        }
    }

    fun confirmRestore(onComplete: (RestoreResult) -> Unit) {
        val content = pendingRestoreContent
        if (content == null) {
            _pendingRestoreSummary.value = null
            return
        }

        viewModelScope.launch {
            _isBusy.value = true
            val result = backupManager.restoreFromJson(content, getApplication())
            _pendingRestoreSummary.value = null
            pendingRestoreContent = null
            _isBusy.value = false
            refreshLocalBackups()
            onComplete(result)
        }
    }

    fun dismissRestoreConfirmation() {
        _pendingRestoreSummary.value = null
        pendingRestoreContent = null
    }
}
