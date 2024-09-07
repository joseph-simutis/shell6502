package io.github.josephsimutis.types.instruction

import io.github.josephsimutis.Virtual6502
import io.github.josephsimutis.types.RawShort

enum class Instruction (val run: (Virtual6502, AddressMode, RawShort) -> Unit) {
    ADC({ v6502, _, address ->
        val input = Pair(v6502.A, v6502.readMemory(address))
        val output = input.first.addWithCarry(input.second, v6502.carryFlag)
        v6502.A = output.first
        v6502.calculateNegative(output.first)
        v6502.calculateZero(output.first)
        v6502.carryFlag = output.second
        v6502.calculateOverflow(input, output.first)
    }),
    AND({ v6502, _, address ->
        v6502.A = v6502.A and v6502.readMemory(address)
        v6502.calculateNegative(v6502.A)
        v6502.calculateZero(v6502.A)
    }),
    ASL({ v6502, addressMode, address ->
        val output = if (addressMode == AddressMode.ACCUMULATOR) {
            val output = v6502.A.shiftLeft(false)
            v6502.A = output.first
            output
        } else {
            val output = v6502.readMemory(address).shiftLeft(false)
            v6502.writeMemory(address, output.first)
            output
        }
        v6502.calculateNegative(output.first)
        v6502.calculateZero(output.first)
        v6502.carryFlag = output.second
    }),
    BCC({ v6502, _, address ->
        if (!v6502.carryFlag) {
            v6502.PC = v6502.PC signedPlus address.low.toByte()
        }
    }),
    BCS({ v6502, _, address ->
        if (v6502.carryFlag) {
            v6502.PC = v6502.PC signedPlus address.low.toByte()
        }
    }),
    BEQ({ v6502, _, address ->
        if (v6502.zeroFlag) {
            v6502.PC = v6502.PC signedPlus address.low.toByte()
        }
    }),
    BIT({ v6502, _, address ->
        val memory = v6502.readMemory(address)
        v6502.calculateZero(v6502.A and memory)
        v6502.calculateNegative(memory)
        v6502.overflowFlag = memory[6]
    }),
    BMI({ v6502, _, address ->
        if (v6502.negativeFlag) {
            v6502.PC = v6502.PC signedPlus address.low.toByte()
        }
    }),
    BNE({ v6502, _, address ->
        if (!v6502.zeroFlag) {
            v6502.PC = v6502.PC signedPlus address.low.toByte()
        }
    }),
    BPL({ v6502, _, address ->
        if (!v6502.negativeFlag) {
            v6502.PC = v6502.PC signedPlus address.low.toByte()
        }
    }),
    BRK({ v6502, _, _ ->
        if (!v6502.interruptDisableFlag) {
            v6502.pushStack(v6502.PC + 2u)
            v6502.pushStack(v6502.P.withFlag(4, true))
            v6502.PC = v6502.readMemory(RawShort.IRQ_BRK)
        }
    }),
    BVC({ v6502, _, address ->
        if (!v6502.overflowFlag) {
            v6502.PC = v6502.PC signedPlus address.low.toByte()
        }
    }),
    BVS({ v6502, _, address ->
        if (v6502.overflowFlag) {
            v6502.PC = v6502.PC signedPlus address.low.toByte()
        }
    }),
    CLC({ v6502, _, _ ->
        v6502.carryFlag = false
    }),
    CLD({ v6502, _, _ ->
        v6502.decimalFlag = false
    }),
    CLI({ v6502, _, _ ->
        v6502.interruptDisableFlag = false
    }),
    CLV({ v6502, _, _ ->
        v6502.overflowFlag = false
    }),
    CMP({ v6502, _, address ->
        val output = v6502.A.subtractWithBorrow(v6502.readMemory(address), true)
        v6502.calculateNegative(output.first)
        v6502.calculateZero(output.first)
        v6502.carryFlag = output.second
    }),
    CPX({ v6502, _, address ->
        val output = v6502.X.subtractWithBorrow(v6502.readMemory(address), true)
        v6502.calculateNegative(output.first)
        v6502.calculateZero(output.first)
        v6502.carryFlag = output.second
    }),
    CPY({ v6502, _, address ->
        val output = v6502.Y.subtractWithBorrow(v6502.readMemory(address), true)
        v6502.calculateNegative(output.first)
        v6502.calculateZero(output.first)
        v6502.carryFlag = output.second
    }),
    DEC({ v6502, _, address ->
        val output = (v6502.readMemory(address) - 1u).low
        v6502.writeMemory(address, output)
        v6502.calculateNegative(output)
        v6502.calculateZero(output)
    }),
    DEX({ v6502, _, _ ->
        v6502.X--
        v6502.calculateNegative(v6502.X)
        v6502.calculateZero(v6502.X)
    }),
    DEY({ v6502, _, _ ->
        v6502.Y--
        v6502.calculateNegative(v6502.Y)
        v6502.calculateZero(v6502.Y)
    }),
    EOR({ v6502, _, address ->
        v6502.A = v6502.A xor v6502.readMemory(address)
        v6502.calculateNegative(v6502.A)
        v6502.calculateZero(v6502.A)
    }),
    INC({ v6502, _, address ->
        val output = (v6502.readMemory(address) + 1u).low
        v6502.writeMemory(address, output)
        v6502.calculateNegative(output)
        v6502.calculateZero(output)
    }),
    INX({ v6502, _, _ ->
        v6502.X++
        v6502.calculateNegative(v6502.X)
        v6502.calculateZero(v6502.X)
    }),
    INY({ v6502, _, _ ->
        v6502.Y++
        v6502.calculateNegative(v6502.Y)
        v6502.calculateZero(v6502.Y)
    }),
    JMP({ v6502, _, address ->
        v6502.PC = address
    }),
    JSR({ v6502, _, address ->
        v6502.pushStack(v6502.PC + 2u)
        v6502.PC = address
    }),
    LDA({ v6502, _, address ->
        v6502.A = v6502.readMemory(address)
        v6502.calculateNegative(v6502.A)
        v6502.calculateZero(v6502.A)
    }),
    LDX({ v6502, _, address ->
        v6502.X = v6502.readMemory(address)
        v6502.calculateNegative(v6502.X)
        v6502.calculateZero(v6502.X)
    }),
    LDY({ v6502, _, address ->
        v6502.Y = v6502.readMemory(address)
        v6502.calculateNegative(v6502.Y)
        v6502.calculateZero(v6502.Y)
    }),
    LSR({ v6502, addressMode, address ->
        val output = if (addressMode == AddressMode.ACCUMULATOR) {
            val output = v6502.A.shiftRight(false)
            v6502.A = output.first
            output
        } else {
            val output = v6502.readMemory(address).shiftRight(false)
            v6502.writeMemory(address, output.first)
            output
        }
        v6502.negativeFlag = false
        v6502.calculateZero(output.first)
        v6502.carryFlag = output.second
    }),
    NOP({ _, _, _ -> }),
    ORA({ v6502, _, address ->
        v6502.A = v6502.A or v6502.readMemory(address)
        v6502.calculateNegative(v6502.A)
        v6502.calculateZero(v6502.A)
    }),
    PHA({ v6502, _, _ ->
        v6502.pushStack(v6502.A)
    }),
    PHP({ v6502, _, _ ->
        v6502.pushStack(v6502.P.withFlag(4, true).withFlag(5, true))
    }),
    PLA({ v6502, _, _ ->
        v6502.A = v6502.pullByteFromStack()
    }),
    PLP({ v6502, _, _ ->
        val bit5 = v6502.P[5]
        val bit4 = v6502.P[4]
        v6502.P = v6502.pullByteFromStack().withFlag(4, bit4).withFlag(5, bit5)
    }),
    ROL({ v6502, addressMode, address ->
        val output = if (addressMode == AddressMode.ACCUMULATOR) {
            val output = v6502.A.shiftLeft(v6502.carryFlag)
            v6502.A = output.first
            output
        } else {
            val output = v6502.readMemory(address).shiftLeft(v6502.carryFlag)
            v6502.writeMemory(address, output.first)
            output
        }
        v6502.calculateNegative(output.first)
        v6502.calculateZero(output.first)
        v6502.carryFlag = output.second
    }),
    ROR({ v6502, addressMode, address ->
        val output = if (addressMode == AddressMode.ACCUMULATOR) {
            val output = v6502.A.shiftRight(v6502.carryFlag)
            v6502.A = output.first
            output
        } else {
            val output = v6502.readMemory(address).shiftRight(v6502.carryFlag)
            v6502.writeMemory(address, output.first)
            output
        }
        v6502.calculateNegative(output.first)
        v6502.calculateZero(output.first)
        v6502.carryFlag = output.second
    }),
    RTI({ v6502, _, _ ->
        val bit5 = v6502.P[5]
        val bit4 = v6502.P[4]
        v6502.P = v6502.pullByteFromStack().withFlag(4, bit4).withFlag(5, bit5)
        v6502.PC = v6502.pullShortFromStack()
    }),
    RTS({ v6502, _, _ ->
        v6502.PC = v6502.pullShortFromStack()
        v6502.PC++
    }),
    SBC({ v6502, _, address ->
        val input = Pair(v6502.A, v6502.readMemory(address))
        val output = input.first.subtractWithBorrow(input.second, v6502.carryFlag)
        v6502.A = output.first
        v6502.calculateNegative(output.first)
        v6502.calculateZero(output.first)
        v6502.carryFlag = output.second
        v6502.calculateOverflow(input, output.first)
    }),
    SEC({ v6502, _, _ ->
        v6502.carryFlag = true
    }),
    SED({ v6502, _, _ ->
        v6502.decimalFlag = true
    }),
    SEI({ v6502, _, _ ->
        v6502.interruptDisableFlag = true
    }),
    STA({ v6502, _, address ->
        v6502.writeMemory(address, v6502.A)
    }),
    STX({ v6502, _, address ->
        v6502.writeMemory(address, v6502.X)
    }),
    STY({ v6502, _, address ->
        v6502.writeMemory(address, v6502.Y)
    }),
    TAX({ v6502, _, _ ->
        v6502.X = v6502.A
        v6502.calculateNegative(v6502.X)
        v6502.calculateZero(v6502.X)
    }),
    TAY({ v6502, _, _ ->
        v6502.Y = v6502.A
        v6502.calculateNegative(v6502.Y)
        v6502.calculateZero(v6502.Y)
    }),
    TSX({ v6502, _, _ ->
        v6502.X = v6502.S
        v6502.calculateNegative(v6502.X)
        v6502.calculateZero(v6502.X)
    }),
    TXA({ v6502, _, _ ->
        v6502.A = v6502.X
        v6502.calculateNegative(v6502.A)
        v6502.calculateZero(v6502.A)
    }),
    TXS({ v6502, _, _ ->
        v6502.S = v6502.X
    }),
    TYA({ v6502, _, _ ->
        v6502.A = v6502.Y
        v6502.calculateNegative(v6502.A)
        v6502.calculateZero(v6502.A)
    });
}