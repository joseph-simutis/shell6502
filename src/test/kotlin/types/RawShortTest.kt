package types

import io.github.josephsimutis.types.RawByte
import io.github.josephsimutis.types.RawShort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RawShortTest {
    private val testShort = RawShort(0xFFFAu)

    @Test
    fun split() {
        assertEquals(Pair(RawByte(0xFAu), RawByte(0xFFu)), testShort.split())
    }

    @Test
    fun toInt() {
        assertEquals(0xFFFA, testShort.toInt())
    }

    @Test
    fun compareTo() {
        assertTrue(testShort > RawShort.ZERO)
        assertTrue(testShort < RawShort.MAX)
    }

    @Test
    fun plus() {
        assertEquals(RawShort.MAX, testShort + RawShort(5u))
        assertEquals(RawShort.ZERO, testShort + RawShort(6u))
        assertEquals(RawShort.MAX, testShort + RawByte(5u))
        assertEquals(RawShort.MAX, testShort + 5u)
    }

    @Test
    fun minus() {
        assertEquals(RawShort(0xFFF9u), testShort - RawShort(1u))
        assertEquals(RawShort(0xFFF9u), testShort - RawByte(1u))
        assertEquals(RawShort(0xFFF9u), testShort - 1u)
    }

    @Test
    fun times() {
        assertEquals(RawShort(4u), RawShort(2u) * RawShort(2u))
        assertEquals(RawShort(4u), RawShort(2u) * RawByte(2u))
        assertEquals(RawShort(4u), RawShort(2u) * 2u)
    }
}