package com.example.util

object TimeConverter {
    fun textToTime(time: String): Int {
        val (hour, minute, second) = time.trim().split(":").map { it.toInt() }
        return hour * 3600 + minute * 60 + second
    }

    fun timeToText(time: Int): String {
        val hour = time / 3600
        val minute = time % 3600 / 60
        val second = time % 60
        return "$hour:$minute:$second"
    }
}