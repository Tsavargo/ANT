package com.example.parser

interface Mapper<T> {
    fun map(line: Map<String, String>): T
}