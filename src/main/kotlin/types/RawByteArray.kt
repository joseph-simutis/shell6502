package io.github.josephsimutis.types

@JvmInline
@OptIn(ExperimentalUnsignedTypes::class)
value class RawByteArray(val data: UByteArray) : Collection<RawByte> {
    constructor(size: Int) : this(UByteArray(size))
    constructor(size: Int, init: (Int) -> RawByte) : this(UByteArray(size) { index -> init(index).data })

    operator fun get(index: Int) = RawByte(data[index])
    operator fun get(index: RawShort) = this[index.toInt()]

    operator fun set(index: Int, value: RawByte) {
        data[index] = value.data
    }

    operator fun set(index: RawShort, value: RawByte) {
        this[index.toInt()] = value
    }

    override val size: Int
        get() = data.size

    override fun isEmpty() = data.isEmpty()

    override operator fun iterator() = Iterator(data)

    class Iterator(private val array: UByteArray) : kotlin.collections.Iterator<RawByte> {
        private var index = 0

        override fun hasNext() = index < array.size
        override fun next() = if (hasNext()) RawByte(array[index++]) else throw NoSuchElementException(index.toString())
    }

    override fun containsAll(elements: Collection<RawByte>) = data.containsAll(elements.map { it.data })

    override fun contains(element: RawByte) = data.contains(element.data)
}