package io.github.josephsimutis.types

@JvmInline
value class RawByte(val data: UByte) : Comparable<RawByte> {
    override fun compareTo(other: RawByte): Int = data.compareTo(other.data)

    fun toRawShort() = RawShort(toUShort())
    fun toUShort() = data.toUShort()

    operator fun plus(other: RawByte) = RawByte((data + other.data).toUByte())
    operator fun plus(other: RawShort) = other + this
    operator fun plus(other: Int) = this + RawByte(other.toUByte())

    companion object {
        val ZERO = RawByte(0x00u)
        val MAX = RawByte(0xFFu)
    }
}