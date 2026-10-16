package com.example.parser

import com.jsoizo.kotlincsv.reader.CsvReader
import com.jsoizo.kotlincsv.reader.readFromFile
import com.jsoizo.kotlincsv.reader.withHeader

class Parser {
    val reader = CsvReader()

    fun read(path: String): Unit {
        reader.readFromFile(path) { rows ->
            val records: List<LinkedHashMap<String, String>> = rows.withHeader().toList()
            records.forEach { record: LinkedHashMap<String, String> ->
                // TODO: implement a universal mapping logic to create class instances from the lines
            }
        }
    }
}