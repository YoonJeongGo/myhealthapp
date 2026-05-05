package com.healthlog.myapplication1.domain.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val FMT = "yyyy-MM-dd"

    fun today(): String = SimpleDateFormat(FMT, Locale.getDefault()).format(Date())

    fun nDaysAgo(n: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -n)
        return SimpleDateFormat(FMT, Locale.getDefault()).format(cal.time)
    }

    fun nMonthsAgo(n: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -n)
        return SimpleDateFormat(FMT, Locale.getDefault()).format(cal.time)
    }

    fun nYearsAgo(n: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -n)
        return SimpleDateFormat(FMT, Locale.getDefault()).format(cal.time)
    }

    // "2025-01-15" → "1월 15일 (수)"
    fun toDisplayFull(dateStr: String): String {
        return try {
            val date = SimpleDateFormat(FMT, Locale.KOREAN).parse(dateStr) ?: return dateStr
            SimpleDateFormat("M월 d일 (E)", Locale.KOREAN).format(date)
        } catch (e: Exception) { dateStr }
    }

    // "2025-01-15" → "1월 15일"
    fun toDisplayShort(dateStr: String): String {
        return try {
            val date = SimpleDateFormat(FMT, Locale.KOREAN).parse(dateStr) ?: return dateStr
            SimpleDateFormat("M월 d일", Locale.KOREAN).format(date)
        } catch (e: Exception) { dateStr }
    }

    // "2025-01-15" → "2025년 1월"
    fun toDisplayMonth(dateStr: String): String {
        return try {
            val date = SimpleDateFormat(FMT, Locale.KOREAN).parse(dateStr) ?: return dateStr
            SimpleDateFormat("yyyy년 M월", Locale.KOREAN).format(date)
        } catch (e: Exception) { dateStr }
    }

    // yyyy-MM 형식으로 해당 월의 첫날/마지막날 반환
    fun monthRange(yearMonth: String): Pair<String, String> {
        val cal = Calendar.getInstance()
        val parts = yearMonth.split("-")
        cal.set(Calendar.YEAR, parts[0].toInt())
        cal.set(Calendar.MONTH, parts[1].toInt() - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val first = SimpleDateFormat(FMT, Locale.getDefault()).format(cal.time)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val last = SimpleDateFormat(FMT, Locale.getDefault()).format(cal.time)
        return first to last
    }

    fun currentYearMonth(): String =
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

    fun prevMonth(ym: String): String {
        val cal = Calendar.getInstance()
        val parts = ym.split("-")
        cal.set(parts[0].toInt(), parts[1].toInt() - 1, 1)
        cal.add(Calendar.MONTH, -1)
        return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.time)
    }

    fun nextMonth(ym: String): String {
        val cal = Calendar.getInstance()
        val parts = ym.split("-")
        cal.set(parts[0].toInt(), parts[1].toInt() - 1, 1)
        cal.add(Calendar.MONTH, 1)
        return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.time)
    }

    fun daysInMonth(ym: String): Int {
        val cal = Calendar.getInstance()
        val parts = ym.split("-")
        cal.set(parts[0].toInt(), parts[1].toInt() - 1, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun firstDayOfWeek(ym: String): Int {
        val cal = Calendar.getInstance()
        val parts = ym.split("-")
        cal.set(parts[0].toInt(), parts[1].toInt() - 1, 1)
        return (cal.get(Calendar.DAY_OF_WEEK) - 1) // 0=Sun, 6=Sat
    }

    fun dateOf(ym: String, day: Int): String = "$ym-${day.toString().padStart(2, '0')}"
}
