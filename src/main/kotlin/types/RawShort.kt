package io.github.josephsimutis.types

@JvmInline
value class RawShort(val data: UShort) : Comparable<RawShort> {
    constructor(uInt: UInt) : this(uInt.toUShort())

    fun split() = Pair(RawByte((data and 0x00FFu).toUByte()), RawByte(((data and 0xFF00u).toInt() ushr 8).toUByte()))

    fun toInt() = data.toInt()

    override fun compareTo(other: RawShort): Int = data.compareTo(other.data)

    operator fun plus(other: RawShort) = RawShort(data + other.data)
    operator fun plus(other: Int) = RawShort(data + other.toUInt())
}