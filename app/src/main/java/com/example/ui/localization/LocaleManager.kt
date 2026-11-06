package com.example.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    BANGLA("bn", "Bangla", "বাংলা"),
    ENGLISH("en", "English", "English")
}

/**
 * Global reactive language controller.
 */
object LanguageController {
    var currentLanguage by mutableStateOf(AppLanguage.BANGLA)
        private set

    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
    }

    fun toggleLanguage() {
        currentLanguage = if (currentLanguage == AppLanguage.BANGLA) AppLanguage.ENGLISH else AppLanguage.BANGLA
    }
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.BANGLA }

@Composable
fun ProvideAppLanguage(
    language: AppLanguage = LanguageController.currentLanguage,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAppLanguage provides language) {
        content()
    }
}

/**
 * Format numbers and currency to Bangladeshi Taka (৳).
 * Converts digits to Bengali numerals when Bangla is selected.
 */
fun formatTaka(amount: Double, language: AppLanguage): String {
    val rounded = if (amount % 1.0 == 0.0) amount.toLong().toString() else String.format("%.2f", amount)
    val formattedDigits = if (language == AppLanguage.BANGLA) {
        toBengaliNumerals(rounded)
    } else {
        rounded
    }
    return "৳ $formattedDigits"
}

fun formatTaka(amount: Long, language: AppLanguage): String {
    val str = amount.toString()
    val formattedDigits = if (language == AppLanguage.BANGLA) {
        toBengaliNumerals(str)
    } else {
        str
    }
    return "৳ $formattedDigits"
}

fun toBengaliNumerals(input: String): String {
    val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val sb = StringBuilder()
    for (ch in input) {
        if (ch in '0'..'9') {
            sb.append(bengaliDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

/**
 * Bilingual String Dictionary for Hardware Manager
 */
object AppStrings {
    fun appName(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "হার্ডওয়্যার ম্যানেজার"
        AppLanguage.ENGLISH -> "Hardware Manager"
    }

    // Navigation Tabs
    fun tabHome(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "হোম"
        AppLanguage.ENGLISH -> "Home"
    }

    fun tabSell(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "বিক্রি"
        AppLanguage.ENGLISH -> "Sell"
    }

    fun tabStock(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "স্টক"
        AppLanguage.ENGLISH -> "Stock"
    }

    fun tabHistory(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "হিস্ট্রি"
        AppLanguage.ENGLISH -> "History"
    }

    fun tabBusiness(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "হিসাব"
        AppLanguage.ENGLISH -> "Business"
    }

    // Screen Titles
    fun titleHome(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "দোকানের ড্যাশবোর্ড"
        AppLanguage.ENGLISH -> "Store Dashboard"
    }

    fun titleSell(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পণ্য বিক্রি"
        AppLanguage.ENGLISH -> "Sell Product"
    }

    fun titleStock(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পণ্য স্টক তালিকা"
        AppLanguage.ENGLISH -> "Inventory Stock"
    }

    fun titleHistory(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "বিক্রির ইতিহাস"
        AppLanguage.ENGLISH -> "Sales History"
    }

    fun titleBusiness(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ব্যবসায়িক হিসাব"
        AppLanguage.ENGLISH -> "Business Analytics"
    }

    // Placeholder Cards Content
    fun placeholderTitle(screenName: String, lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "$screenName স্ক্রিন"
        AppLanguage.ENGLISH -> "$screenName Screen"
    }

    fun placeholderSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পরবর্তী ধাপে এখানে রুম ডাটাবেজ যুক্ত হবে।"
        AppLanguage.ENGLISH -> "Room database will be connected in next phase."
    }

    fun placeholderInfoBadge(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "প্লেসহোল্ডার ভিউ"
        AppLanguage.ENGLISH -> "Placeholder View"
    }

    fun actionSampleButton(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "নমুনা অ্যাকশন (৪৪dp)"
        AppLanguage.ENGLISH -> "Sample Action (44dp)"
    }

    // Settings & Language Dialog
    fun settings(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সেটিংস ও ভাষা"
        AppLanguage.ENGLISH -> "Settings & Language"
    }

    fun selectLanguage(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ভাষা নির্বাচন করুন"
        AppLanguage.ENGLISH -> "Select Language"
    }

    fun close(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "বন্ধ করুন"
        AppLanguage.ENGLISH -> "Close"
    }

    fun currentStoreMode(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "অফলাইন লোকাল ডাটাবেজ (রুম)"
        AppLanguage.ENGLISH -> "Offline Local Database (Room)"
    }

    fun activeCurrency(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "টাকা (৳) কারেন্সি সক্রিয়"
        AppLanguage.ENGLISH -> "Taka (৳) Currency Active"
    }
}
