package io.github.josephsimutis

import io.github.josephsimutis.types.RawByte
import io.github.josephsimutis.types.RawByteArray
import io.github.josephsimutis.types.RawShort
import io.github.josephsimutis.types.instruction.AddressMode
import io.github.josephsimutis.types.instruction.Operation

class Virtual6502() {
    // Accumulator
    var A = RawByte(0x00u)
    // Status Register
    var P = RawByte(0x00u)
    // Program Counter
    var PC = RawShort(0x0000u)
    // Stack Pointer
    var S = RawByte(0x00u)
    // Index Register 1
    var X = RawByte(0x00u)
    // Index Register 2
    var Y = RawByte(0x00u)
    // Do not read or write to this directly, instead use the readMemory and writeMemory methods instead, as those respect memory mapping.
    private val memory = RawByteArray(0xFFFF)

    private var isInitialized = false

    //Make sure to run this before anything else.
    fun initialize() {
        A = readMemory(RawShort(0x0000u))
        P = RawByte.ZERO
        PC = readMemory(RawShort(0xFFFCu)) combine readMemory(RawShort(0xFFFDu))
        S = RawByte.MAX
        X = readMemory(RawShort(0x0000u))
        Y = readMemory(RawShort(0x0000u))
        isInitialized = true
    }

    fun readMemory(address: RawShort): RawByte {
        return memory[address]
    }

    fun writeMemory(address: RawShort, value: RawByte) {
        memory[address] = value
    }

    fun clockCycle(): Boolean {
        if (!isInitialized) return false
        val instruction = decodeInstruction()
        runInstruction(instruction.first, instruction.second)
        PC += instruction.second.instructionLength
        return true
    }

    private fun decodeInstruction(): Pair<Operation, AddressMode> {}

    private fun runInstruction(operation: Operation, addressMode: AddressMode) {
        operation.run(this, addressMode.getAddress(this, readMemory(PC+1) combine readMemory(PC+2)))
    }
}