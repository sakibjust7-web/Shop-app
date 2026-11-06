package com.example.data.local

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LanguageController

enum class ViewMode(val titleEn: String, val titleBn: String, val descEn: String, val descBn: String) {
    SIMPLE(
        titleEn = "Simple View",
        titleBn = "সহজ ভিউ",
        descEn = "Clean layout with larger buttons and essential details for fast-paced shop counter sales.",
        descBn = "দ্রুত বিক্রির জন্য পরিষ্কার ও বড় বাটনযুক্ত সহজ স্ক্রিন।"
    ),
    DETAILED(
        titleEn = "Detailed View",
        titleBn = "বিস্তারিত ভিউ",
        descEn = "Comprehensive view showing buying prices, profit margins, stock levels, timestamps and notes.",
        descBn = "ক্রয়মূল্য, আনুমানিক লাভ, নির্দিষ্ট স্টক অ্যালার্ট ও বিস্তারিত তথ্যসহ স্ক্রিন।"
    )
}

object AppSettings {
    var viewMode by mutableStateOf(ViewMode.DETAILED)
        private set

    fun setViewMode(mode: ViewMode, context: Context? = null) {
        viewMode = mode
        if (context != null) {
            val prefs = context.getSharedPreferences("hardware_store_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("view_mode", mode.name).apply()
        }
    }

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("hardware_store_prefs", Context.MODE_PRIVATE)
        val modeStr = prefs.getString("view_mode", ViewMode.DETAILED.name)
        viewMode = try {
            ViewMode.valueOf(modeStr ?: ViewMode.DETAILED.name)
        } catch (e: Exception) {
            ViewMode.DETAILED
        }

        val langCode = prefs.getString("selected_language", null)
        if (langCode != null) {
            val lang = if (langCode == "en") AppLanguage.ENGLISH else AppLanguage.BANGLA
            LanguageController.setLanguage(lang)
        }
    }

    fun setLanguage(context: Context?, language: AppLanguage) {
        LanguageController.setLanguage(language)
        if (context != null) {
            val prefs = context.getSharedPreferences("hardware_store_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("selected_language", language.code).apply()
        }
    }
}
