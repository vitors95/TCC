package com.example.vitors.tcc_kotlin.Utils

import android.util.Log
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateHelper {

    fun dateString2Timetamp(dateString: String): Long {
        var dateTime = dateString.split(" ")
        dateTime = listOf(dateTime[0] + "T" + dateTime[1])
        val localDateTime = LocalDateTime.parse(dateTime[0])
        val timestamp = localDateTime.atZone(ZoneOffset.UTC).toEpochSecond() - Constants.ZONE_OFFSET
        return timestamp
    }

    fun timestamp2LocalDateTime(timestamp: Long): LocalDateTime {
        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC)
        return localDateTime
    }

}