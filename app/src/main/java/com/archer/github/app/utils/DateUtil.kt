package com.archer.github.app.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateUtil {

    private val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")

    fun dateFormat(dateStr: String): String {
        val zonedDateTime = ZonedDateTime.parse(dateStr)
        return try {
            zonedDateTime.format(formatter)
        } catch (ex: Exception) {
            dateStr
        }
    }
}
