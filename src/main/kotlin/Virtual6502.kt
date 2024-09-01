package io.github.josephsimutis

import io.github.josephsimutis.types.RawByte
import io.github.josephsimutis.types.RawByteArray
import io.github.josephsimutis.types.RawShort
import io.github.josephsimutis.types.instruction.AddressMode
import io.github.josephsimutis.types.instruction.Operation

class Virtual6502() {
    var A = RawByte(0x00u)
    var P = RawByte(0x00u)
    var PC = RawShort(0x0000u)
    var S = RawByte(0x00u)
    var X = RawByte(0x00u)
    var Y = RawByte(0x00u)

    private val memory = RawByteArray(0xFFFF)

    fun initialize() {}

    fun readMemory() {}

    fun writeMemory() {}

    fun clockCycle() {
        val instruction = decodeInstruction()
        runInstruction(instruction.first, instruction.second)
        PC += instruction.second.instructionLength
    }

    fun decodeInstruction() : Pair<Operation, AddressMode> {}

    fun runInstruction(operation: Operation, addressMode: AddressMode) {
        addressMode.getAddress(this, memory[PC+1] combine memory[PC+2])
    }
}