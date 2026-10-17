package com.example.parser

import com.jsoizo.kotlincsv.reader.CsvReader
import com.jsoizo.kotlincsv.reader.readFromFile
import com.jsoizo.kotlincsv.reader.withHeader

class Parser {
    val reader = CsvReader()

    fun <T> read(path: String, mapper: RecordMapper<T>): List<T> =
        reader.readFromFile(path) { rows ->
            rows.withHeader().map {
                mapper.map(it)
            }.toList()
        }
}