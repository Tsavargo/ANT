package com.example.parser

fun interface RecordMapper<T> {
    fun map(record: LinkedHashMap<String, String>): T
}