package io.github.josephsimutis.types

@JvmInline
@OptIn(ExperimentalStdlibApi::class)
value class RawByte(val data: UByte) : Comparable<RawByte> {
    override fun compareTo(other: RawByte): Int = data.compareTo(other.data)

    fun toRawShort() = RawShort(toUShort())
    fun toUShort() = data.toUShort()
    fun toUInt() = data.toUInt()
    fun toInt() = data.toInt()

    operator fun plus(other: RawByte) = RawShort(data + other.data)
    operator fun plus(other: RawShort) = this.toRawShort() + other
    operator fun plus(other: UInt) = this + RawByte(other.toUByte())

    operator fun minus(other: RawByte) = RawShort(data - other.data)
    operator fun minus(other: RawShort) = this.toRawShort() - other
    operator fun minus(other: UInt) = this - RawByte(other.toUByte())

    operator fun times(other: RawByte) = RawShort(data * other.data)
    operator fun times(other: RawShort) = this.toRawShort() * other
    operator fun times(other: UInt) = this * RawByte(other.toUByte())

    operator fun get(index: Int) = ((toInt() ushr index) and 1) > 0

    // Note: As RawBytes are not mutable, you will need to set the byte manually.
    operator fun set(index: Int, value: Boolean) = if (value) {
        RawByte(data or (1 shl index).toUByte())
    } else {
        RawByte(data and (1 shl index).inv().toUByte())
    }

    override fun toString() = data.toHexString(HexFormat.UpperCase)

    companion object {
        val ZERO = RawByte(0x00u)
        val MAX = RawByte(0xFFu)
    }
}