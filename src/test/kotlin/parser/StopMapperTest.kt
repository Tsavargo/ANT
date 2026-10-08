package com.example.parser

import com.example.models.Stop
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StopMapperTest {
    val stopMapper = StopMapper()
    val line: Map<String, String> = mapOf(
        "stop_id"   to "008605",
        "stop_name" to "Kolosy tér",
        "stop_lat"  to "47.527543",
        "stop_lon"  to "19.036916"
    )
    val wrongLine = mapOf(
        "stopID"   to "008605",
        "stopName" to "Kolosy tér",
        "latitude"  to "47.527543",
        "longitude"  to "19.036916"
    )

    @Test
    fun `map returns Stop with correct fields for valid line`() {
        val stop: Stop = stopMapper.map(line)
        assertEquals("008605", stop.stopID)
        assertEquals("Kolosy tér", stop.stopName)
        assertEquals(47.527543, stop.latitude)
        assertEquals(19.036916, stop.longitude)
    }

    @Test
    fun `map throws NoSuchElementException when GTFS keys are missing`() {
        assertFailsWith<NoSuchElementException> {
            stopMapper.map(wrongLine)
        }
    }
}