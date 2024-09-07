package io.github.josephsimutis.types

@JvmInline
@OptIn(ExperimentalStdlibApi::class)
value class RawByte(val data: UByte) : Comparable<RawByte> {
    override fun compareTo(other: RawByte): Int = data.compareTo(other.data)

    fun toRawShort() = RawShort(toUShort())
    fun toUShort() = data.toUShort()
    fun toUInt() = data.toUInt()
    fun toInt() = data.toInt()
    fun toByte() = data.toByte()

    operator fun plus(other: RawByte) = RawShort(data + other.data)
    operator fun plus(other: RawShort) = this.toRawShort() + other
    operator fun plus(other: UInt) = this + RawByte(other.toUByte())

    operator fun minus(other: RawByte) = RawShort(data - other.data)
    operator fun minus(other: RawShort) = this.toRawShort() - other
    operator fun minus(other: UInt) = this - RawByte(other.toUByte())

    operator fun times(other: RawByte) = RawShort(data * other.data)
    operator fun times(other: RawShort) = this.toRawShort() * other
    operator fun times(other: UInt) = this * RawByte(other.toUByte())

    operator fun inc() = (this + 1u).low
    operator fun dec() = (this - 1u).low

    operator fun get(index: Int) = ((toInt() ushr index) and 1) > 0

    fun withFlag(index: Int, value: Boolean) = if (value) {
        RawByte(data or (1 shl index).toUByte())
    } else {
        RawByte(data and (1 shl index).inv().toUByte())
    }

    infix fun and(other: RawByte) = RawByte(data and other.data)
    infix fun or(other: RawByte) = RawByte(data or other.data)
    infix fun xor(other: RawByte) = RawByte(data xor other.data)
    operator fun not() = RawByte(data.inv())

    fun addWithCarry(other: RawByte, carry: Boolean) : Pair<RawByte, Boolean> {
        val output = toRawShort() + other.toRawShort() + if (carry) 1u else 0u
        return Pair(output.low, output.data > UByte.MAX_VALUE)
    }

    fun subtractWithBorrow(other: RawByte, carry: Boolean) : Pair<RawByte, Boolean> = addWithCarry(!other, carry)

    fun shiftLeft(carry: Boolean): Pair<RawByte, Boolean> {
        val output = (data.toUInt() shl 1) + if (carry) 1u else 0u
        return Pair(RawByte(output.toUByte()), output > UByte.MAX_VALUE)
    }

    fun shiftRight(carry: Boolean): Pair<RawByte, Boolean> {
        val output = (data.toUInt() shr 1) + if (carry) 128u else 0u
        return Pair(RawByte(output.toUByte()), this[0])
    }

    override fun toString() = data.toHexString(HexFormat.UpperCase)

    companion object {
        val ZERO = RawByte(0x00u)
        val ONE = RawByte(0x01u)
        val MAX = RawByte(0xFFu)
    }
}