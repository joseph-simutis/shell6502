package io.github.josephsimutis.types

@JvmInline
value class RawByte(val data: UByte) : Comparable<RawByte> {
    override fun compareTo(other: RawByte): Int = data.compareTo(other.data)

    fun toRawShort() = RawShort(toUShort())
    fun toUShort() = data.toUShort()

    operator fun plus(other: RawByte) = RawShort(data + other.data)
    operator fun plus(other: RawShort) = this.toRawShort() + other
    operator fun plus(other: UInt) = this + RawByte(other.toUByte())

    operator fun minus(other: RawByte) = RawShort(data - other.data)
    operator fun minus(other: RawShort) = this.toRawShort() - other
    operator fun minus(other: UInt) = this - RawByte(other.toUByte())

    operator fun times(other: RawByte) = RawShort(data * other.data)
    operator fun times(other: RawShort) = this.toRawShort() * other
    operator fun times(other: UInt) = this * RawByte(other.toUByte())

    companion object {
        val ZERO = RawByte(0x00u)
        val MAX = RawByte(0xFFu)
    }
}