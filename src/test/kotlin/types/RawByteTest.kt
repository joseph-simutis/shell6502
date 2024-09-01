package types

import io.github.josephsimutis.types.RawByte
import io.github.josephsimutis.types.RawShort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RawByteTest {
    private val testByte = RawByte(0x03u)

    @Test
    fun compareTo() {
        assertTrue(testByte > RawByte.ZERO)
        assertTrue(testByte < RawByte.MAX)
    }

    @Test
    fun toRawShort() {
        assertEquals(RawShort(0x0003u), testByte.toRawShort())
    }

    @Test
    fun toUShort() {
        assertEquals(0x0003u.toUShort(), testByte.toUShort())
    }

    @Test
    fun plus() {
        assertEquals(RawShort(0x0004u), testByte + RawByte(1u))
        assertEquals(RawShort(0x0100u), testByte + RawByte(0xFDu))
        assertEquals(RawShort(0x0004u), testByte + RawShort(1u))
        assertEquals(RawShort(0x0004u), testByte + 1u)
    }

    @Test
    fun minus() {
        assertEquals(RawShort(0x0001u), testByte - RawByte(2u))
        assertEquals(RawShort(0xFFFFu), testByte - RawByte(4u))
        assertEquals(RawShort(0x0001u), testByte - RawShort(2u))
        assertEquals(RawShort(0x0001u), testByte - 2u)
    }

    @Test
    fun times() {
        assertEquals(RawShort(0x0006u), testByte * RawByte(2u))
        assertEquals(RawShort(0x0006u), testByte * RawShort(2u))
        assertEquals(RawShort(0x0006u), testByte * 2u)
    }
}