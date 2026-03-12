package com.fitnesstracker.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val shortFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun formatDate(epochMs: Long): String = displayFormat.format(Date(epochMs))
    fun formatDateShort(epochMs: Long): String = shortFormat.format(Date(epochMs))

    fun todayEpoch(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
