package io.github.josephsimutis.types

@JvmInline
@OptIn(ExperimentalStdlibApi::class)
value class RawShort(val data: UShort) : Comparable<RawShort> {
    constructor(uInt: UInt) : this(uInt.toUShort())
    constructor(uByte: UByte) : this(uByte.toUShort())
    constructor(low: RawByte, high: RawByte) : this((low + high * RawByte.MAX).data)

    val low: RawByte
        get() = RawByte((data and 0x00FFu).toUByte())

    val high: RawByte
        get() = RawByte(((data and 0xFF00u).toInt() ushr 8).toUByte())

    fun split() = Pair(low, high)

    fun toInt() = data.toInt()

    override fun compareTo(other: RawShort): Int = data.compareTo(other.data)

    operator fun plus(other: RawShort) = RawShort(data + other.data)
    operator fun plus(other: RawByte) = this + other.toRawShort()
    operator fun plus(other: UInt) = this + RawShort(other)

    operator fun minus(other: RawShort) = RawShort(data - other.data)
    operator fun minus(other: RawByte) = this - other.toRawShort()
    operator fun minus(other: UInt) = this - RawShort(other)

    operator fun times(other: RawShort) = RawShort(data * other.data)
    operator fun times(other: RawByte) = this * other.toRawShort()
    operator fun times(other: UInt) = this * RawShort(other)

    override fun toString() = "[L:$low,H:$high]"

    companion object {
        val ZERO = RawShort(0x0000u)
        val MAX = RawShort(0xFFFFu)
        val NMI = Pair(RawShort(0xFFFAu), RawShort(0xFFFBu))
        val RESET = Pair(RawShort(0xFFFCu), RawShort(0xFFFDu))
        val IRQ_BRK = Pair(RawShort(0xFFFEu), MAX)
    }
}