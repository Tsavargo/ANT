package com.example.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RouteTypeTest {

    @Test
    fun `fromCode returns matching type for every valid code`() {
        assertEquals(RouteType.TRAM, RouteType.fromCode(0))
        assertEquals(RouteType.METRO, RouteType.fromCode(1))
        assertEquals(RouteType.RAIL, RouteType.fromCode(2))
        assertEquals(RouteType.BUS, RouteType.fromCode(3))
        assertEquals(RouteType.FERRY, RouteType.fromCode(4))
        assertEquals(RouteType.TROLLEY, RouteType.fromCode(11))
    }

    @Test
    fun `fromCode round trips with code for all entries`() {
        for (type in RouteType.entries) {
            assertEquals(type, RouteType.fromCode(type.code))
        }
    }

    @Test
    fun `fromCode rejects unknown codes`() {
        val exception = assertFailsWith<IllegalArgumentException> { RouteType.fromCode(99) }
        assertEquals("Invalid route type: 99", exception.message)

        assertFailsWith<IllegalArgumentException> { RouteType.fromCode(-1) }
        assertFailsWith<IllegalArgumentException> { RouteType.fromCode(5) }
    }
}
