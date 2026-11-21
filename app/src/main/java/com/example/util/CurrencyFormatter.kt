package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CurrencyScale(val label: String, val shortUnit: String, val divisor: Long) {
    OLD_SYP("الليرة السورية (الأسعار الكاملة)", "ل.س", 1L),
    NEW_SYP("الليرة الجديدة (÷ 100)", "ل.س جديدة", 100L)
}

object CurrencyFormatter {
    private val integerFormat: NumberFormat = NumberFormat.getIntegerInstance(Locale.US)

    fun format(amountOldSyp: Long, scale: CurrencyScale, includeUnit: Boolean = true): String {
        val unit = if (includeUnit) " ${scale.shortUnit}" else ""
        return if (scale == CurrencyScale.OLD_SYP) {
            "${integerFormat.format(amountOldSyp)}$unit"
        } else {
            val whole = amountOldSyp / 100L
            val remainder = kotlin.math.abs(amountOldSyp % 100L)
            val numberStr = if (remainder == 0L) {
                integerFormat.format(whole)
            } else {
                val frac = remainder.toString().padStart(2, '0').trimEnd('0')
                "${integerFormat.format(whole)}.$frac"
            }
            "$numberStr$unit"
        }
    }

    fun toInputString(amountOldSyp: Long, scale: CurrencyScale): String {
        return if (scale == CurrencyScale.OLD_SYP) {
            amountOldSyp.toString()
        } else {
            val whole = amountOldSyp / 100L
            val rem = kotlin.math.abs(amountOldSyp % 100L)
            if (rem == 0L) whole.toString() else "$whole.${rem.toString().padStart(2, '0').trimEnd('0')}"
        }
    }

    fun parseInputToOldSyp(raw: String, scale: CurrencyScale): Long? {
        val normalized = raw.trim()
            .replace(",", "")
            .replace("٬", "")
            .replace("٫", ".")
        if (normalized.isEmpty()) return null
        return if (scale == CurrencyScale.OLD_SYP) {
            normalized.toDoubleOrNull()?.toLong()?.coerceAtLeast(0L)
        } else {
            normalized.toDoubleOrNull()?.let { Math.round(it * 100.0).coerceAtLeast(0L) }
        }
    }

    fun formatDateTime(epochMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd  hh:mm a", Locale.forLanguageTag("ar"))
        return sdf.format(Date(epochMillis))
    }
}
