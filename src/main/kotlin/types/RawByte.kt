package io.github.josephsimutis.types

@JvmInline
value class RawByte(val data: UByte) : Comparable<RawByte> {
    infix fun combine(other: RawByte) =
        RawShort((data.toUShort() + other.data.toUShort() * 0x00FFu.toUShort()).toUShort())

    override fun compareTo(other: RawByte): Int = data.compareTo(other.data)

    fun toRawShort() = this combine RawByte(0x00u)

    companion object {
        val ZERO = RawByte(0x00u)
        val MAX = RawByte(0xFFu)
    }
}