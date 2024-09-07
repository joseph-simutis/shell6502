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
        set(value) { P = P.withFlag(7, value) }
    var overflowFlag: Boolean
        get() = P[6]
        set(value) { P = P.withFlag(6, value) }
    var breakFlag: Boolean
        get() = P[4]
        set(value) { P = P.withFlag(4, value) }
    var decimalFlag: Boolean
        get() = P[3]
        set(value) { P = P.withFlag(3, value) }
    var interruptDisableFlag: Boolean
        get() = P[2]
        set(value) { P = P.withFlag(2, value) }
    var zeroFlag: Boolean
        get() = P[1]
        set(value) { P = P.withFlag(1, value) }
    var carryFlag: Boolean
        get() = P[0]
        set(value) { P = P.withFlag(0, value) }

    private var isOn = false

    //Make sure to run this before anything else.
    fun on() {
        isOn = true
        A = readMemory(RawShort.ZERO)
        P = RawByte.ZERO
        PC = readMemory(RawShort.RESET)
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

    fun readMemory(address: Pair<RawShort, RawShort>) = readMemory(address.first, address.second)

    fun writeMemory(address: RawShort, value: RawByte) {
        memory[address] = value
    }

    fun clockCycle(): Boolean {
        if (!isOn) return false
        val instruction = decodeInstruction(readMemory(PC)) ?: return false
        runInstruction(instruction)
        PC += instruction.second.instructionLength
        return true
    }

    private fun decodeInstruction(byte: RawByte): Pair<Instruction, AddressMode> {
        val halves = Pair(byte and RawByte(0xFu), RawByte(byte.data.toUInt().shr(4).toUByte()))
        if (halves.first == RawByte(0x8u)) {
            return Pair(when(halves.second) {
                RawByte.ZERO -> Instruction.PHP
                RawByte.ONE -> Instruction.CLC
                RawByte(0x2u) -> Instruction.PLP
                RawByte(0x3u) -> Instruction.SEC
                RawByte(0x4u) -> Instruction.PHA
                RawByte(0x5u) -> Instruction.CLI
                RawByte(0x6u) -> Instruction.PLA
                RawByte(0x7u) -> Instruction.SEI
                RawByte(0x8u) -> Instruction.DEY
                RawByte(0x9u) -> Instruction.TYA
                RawByte(0xAu) -> Instruction.TAY
                RawByte(0xBu) -> Instruction.CLV
                RawByte(0xCu) -> Instruction.INY
                RawByte(0xDu) -> Instruction.CLD
                RawByte(0xEu) -> Instruction.INX
                RawByte(0xFu) -> Instruction.SED
                else -> Instruction.ERROR
            }, AddressMode.IMPLIED)
        } else if (halves.first == RawByte(0xAu) && halves.second > RawByte(0x7u)) {
            return Pair(when(halves.second) {
                RawByte(0x8u) -> Instruction.TXA
                RawByte(0x9u) -> Instruction.TXS
                RawByte(0xAu) -> Instruction.TAX
                RawByte(0xBu) -> Instruction.TSX
                RawByte(0xCu) -> Instruction.DEC
                RawByte(0xEu) -> Instruction.NOP
                else -> Instruction.ERROR
            }, AddressMode.IMPLIED)
        } else {
            val aaa = RawByte((byte and RawByte(0xE0u)).toUInt().shr(5).toUByte())
            val bbb = RawByte((byte and RawByte(0x1Cu)).toUInt().shr(2).toUByte())
            val cc = byte and RawByte(0x3u)

            return when(cc) {
                RawByte.ONE -> Pair(when(aaa) {
                    RawByte.ZERO -> Instruction.ORA
                    RawByte.ONE -> Instruction.AND
                    RawByte(0x2u) -> Instruction.EOR
                    RawByte(0x3u) -> Instruction.ADC
                    RawByte(0x4u) -> Instruction.STA
                    RawByte(0x5u) -> Instruction.LDA
                    RawByte(0x6u) -> Instruction.CMP
                    RawByte(0x7u) -> Instruction.SBC
                    else -> Instruction.ERROR
                }, when(bbb) {
                    RawByte.ZERO -> AddressMode.PRE_INDIRECT_X
                    RawByte.ONE -> AddressMode.POST_INDIRECT_Y
                    RawByte(0x2u) -> AddressMode.ZERO_PAGE
                    RawByte(0x3u) -> AddressMode.ZERO_PAGE_X
                    RawByte(0x4u) -> AddressMode.IMMEDIATE
                    RawByte(0x5u) -> AddressMode.ABSOLUTE_Y
                    RawByte(0x6u) -> AddressMode.ABSOLUTE
                    RawByte(0x7u) -> AddressMode.ABSOLUTE_X
                    else -> AddressMode.ERROR
                })
                RawByte(0x2u) -> Pair(when(aaa) {}, when(bbb) {})
                RawByte(0x3u) -> {}
                else -> Pair(Instruction.ERROR, AddressMode.ERROR)
            }
        }
    }

    private fun runInstruction(instruction: Instruction, addressMode: AddressMode) {
        instruction.run(this, addressMode, addressMode.getAddress(this, readMemory(PC + 1u, PC + 2u)))
    }

    private fun runInstruction(instruction: Pair<Instruction, AddressMode>) = runInstruction(instruction.first, instruction.second)

    fun pushStack(byte: RawByte) {
        writeMemory(RawShort(S, RawByte.ONE), byte)
        S--
    }

    fun pushStack(short: RawShort) {
        pushStack(short.low)
        pushStack(short.high)
    }

    fun pullByteFromStack(): RawByte {
        S++
        return readMemory(RawShort(S, RawByte.ONE))
    }

    fun pullShortFromStack(): RawShort {
        val low = pullByteFromStack()
        val high = pullByteFromStack()
        return RawShort(low, high)
    }

    fun calculateNegative(value: RawByte) {
        if (!isOn) return
        negativeFlag = value[7]
    }

    fun calculateZero(value: RawByte) {
        if (!isOn) return
        zeroFlag = value == RawByte.ZERO
    }

    fun calculateOverflow(input: Pair<RawByte, RawByte>, output: RawByte) {
        if (!isOn) return
        overflowFlag = (input.first[7] == input.second[7]) && (input.first[7] != output[7])
    }
}