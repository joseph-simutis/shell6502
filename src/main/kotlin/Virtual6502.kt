package io.github.josephsimutis

import io.github.josephsimutis.types.RawByte
import io.github.josephsimutis.types.RawByteArray
import io.github.josephsimutis.types.RawShort
import io.github.josephsimutis.types.instruction.AddressMode
import io.github.josephsimutis.types.instruction.Instruction

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
    private val memory = RawByteArray(0xFFFF + 1)

    var negativeFlag: Boolean
        get() = P[7]
        set(value) { P[7] = value }
    var overflowFlag: Boolean
        get() = P[6]
        set(value) { P[6] = value }
    var breakFlag: Boolean
        get() = P[4]
        set(value) { P[4] = value }
    var decimalFlag: Boolean
        get() = P[3]
        set(value) { P[3] = value }
    var interruptDisableFlag: Boolean
        get() = P[2]
        set(value) { P[2] = value }
    var zeroFlag: Boolean
        get() = P[1]
        set(value) { P[1] = value }
    var carryFlag: Boolean
        get() = P[0]
        set(value) { P[0] = value }

    private var isOn = false

    //Make sure to run this before anything else.
    fun on() {
        isOn = true
        A = readMemory(RawShort.ZERO)
        P = RawByte.ZERO
        PC = readMemory(RawShort.RESET.first, RawShort.RESET.second)
        S = RawByte(0xFDu)
        X = readMemory(RawShort.ZERO)
        Y = readMemory(RawShort.ZERO)
    }

    fun off() {
        isOn = false
    }

    fun hardReset() {
        off()
        on()
    }

    fun readMemory(address: RawShort): RawByte {
        return memory[address]
    }

    fun readMemory(addressLow: RawShort, addressHigh: RawShort) =
        RawShort(readMemory(addressLow), readMemory(addressHigh))

    fun writeMemory(address: RawShort, value: RawByte) {
        memory[address] = value
    }

    fun clockCycle(): Boolean {
        if (!isOn) return false
        val instruction = decodeInstruction(readMemory(PC))
        runInstruction(instruction.first, instruction.second)
        PC += instruction.second.instructionLength
        return true
    }

    private fun decodeInstruction(byte: RawByte): Pair<Instruction, AddressMode> {
        TODO()
    }

    private fun runInstruction(instruction: Instruction, addressMode: AddressMode) {
        instruction.run(this, addressMode.getAddress(this, readMemory(PC + 1u, PC + 2u)))
    }
}