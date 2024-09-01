package io.github.josephsimutis.types

@JvmInline
value class RawShort(val data: UShort) : Comparable<RawShort> {
    constructor(int: Int) : this(int.toUShort())
    constructor(uInt: UInt) : this(uInt.toUShort())
    constructor(low: RawByte, high: RawByte) : this((low + high * RawByte.MAX).toUShort())

    fun split() = Pair(RawByte((data and 0x00FFu).toUByte()), RawByte(((data and 0xFF00u).toInt() ushr 8).toUByte()))

    fun toInt() = data.toInt()

    override fun compareTo(other: RawShort): Int = data.compareTo(other.data)

    operator fun plus(other: RawShort) = RawShort(data + other.data)
    operator fun plus(other: RawByte) = this + other.toRawShort()
    operator fun plus(other: Int) = this + RawShort(other)

    companion object {
        val ZERO = RawShort(0x0000u)
        val NMI = Pair(RawShort(0xFFFAu), RawShort(0xFFFBu))
        val RESET = Pair(RawShort(0xFFFCu), RawShort(0xFFFDu))
        val IRQ = Pair(RawShort(0xFFFEu), RawShort(0xFFFFu))
        val BRK = IRQ
    }
}