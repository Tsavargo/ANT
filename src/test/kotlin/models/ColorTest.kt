package com.example.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ColorTest {

    @Test
    fun `toHex pads with leading zeros and uses uppercase`() {
        assertEquals("009EE3", Color(0x009EE3).toHex())
        assertEquals("0000FF", Color(0x0000FF).toHex())
        assertEquals("000000", Color(0x000000).toHex())
        assertEquals("FFFFFF", Color(0xFFFFFF).toHex())
    }

    @Test
    fun `toString prefixes hex with hash`() {
        assertEquals("#009EE3", Color(0x009EE3).toString())
    }

    @Test
    fun `fromHex accepts with and without hash`() {
        assertEquals(Color(0x009EE3), Color.fromHex("009EE3"))
        assertEquals(Color(0x009EE3), Color.fromHex("#009EE3"))
    }

    @Test
    fun `fromHex accepts lowercase and surrounding whitespace`() {
        assertEquals(Color(0x009EE3), Color.fromHex("009ee3"))
        assertEquals(Color(0x009EE3), Color.fromHex("  009EE3 "))
    }

    @Test
    fun `fromHex and toHex round trip`() {
        val hex = "1E90FF"
        assertEquals(hex, Color.fromHex(hex).toHex())
    }

    @Test
    fun `fromHex rejects wrong length`() {
        assertFailsWith<IllegalArgumentException> { Color.fromHex("9EE3") }
        assertFailsWith<IllegalArgumentException> { Color.fromHex("FF009EE3") }
        assertFailsWith<IllegalArgumentException> { Color.fromHex("") }
        assertFailsWith<IllegalArgumentException> { Color.fromHex("9EE3F") }
        assertFailsWith<IllegalArgumentException> { Color.fromHex("009EE3F") }
    }

    @Test
    fun `fromHex rejects input that is empty after prefix or trim`() {
        assertFailsWith<IllegalArgumentException> { Color.fromHex("#") }
        assertFailsWith<IllegalArgumentException> { Color.fromHex("   ") }
    }

    @Test
    fun `fromHex rejects non-hex characters`() {
        assertFailsWith<IllegalArgumentException> { Color.fromHex("GGGGGG") }
    }

    @Test
    fun `fromHex rejects signs`() {
        assertFailsWith<IllegalArgumentException> { Color.fromHex("+FFFFF") }
        assertFailsWith<IllegalArgumentException> { Color.fromHex("-FFFFF") }
    }

    @Test
    fun `fromHex rejects unsupported prefixes`() {
        assertFailsWith<IllegalArgumentException> { Color.fromHex("##FFFFF") }
        assertFailsWith<IllegalArgumentException> { Color.fromHex("0x9EE3") }
    }

    @Test
    fun `fromHex rejects inner whitespace`() {
        assertFailsWith<IllegalArgumentException> { Color.fromHex("009 E3") }
        assertFailsWith<IllegalArgumentException> { Color.fromHex("# 9EE3F") }
    }

    @Test
    fun `constructor rejects values outside 24 bits`() {
        assertFailsWith<IllegalArgumentException> { Color(0x1000000) }
        assertFailsWith<IllegalArgumentException> { Color(-1) }
    }
    
    @Test
    fun `constructor rejects extreme int values`() {
        assertFailsWith<IllegalArgumentException> { Color(Int.MAX_VALUE) }
        assertFailsWith<IllegalArgumentException> { Color(Int.MIN_VALUE) }
    }
}
