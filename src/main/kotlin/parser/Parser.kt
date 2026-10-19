package com.example.parser

import com.jsoizo.kotlincsv.reader.CsvReader
import com.jsoizo.kotlincsv.reader.readFromFile
import com.jsoizo.kotlincsv.reader.withHeader
import java.io.File
import java.io.FileNotFoundException

class Parser {
    val reader = CsvReader()

    fun <T> read(path: String, mapper: RecordMapper<T>): List<T> {
        if (!File(path).exists()) {
            throw FileNotFoundException("Missing GTFS file: $path")
        }
        return reader.readFromFile(path) { rows ->
            rows.withHeader().map {
                mapper.map(it)
            }.toList()
        }
    }
}