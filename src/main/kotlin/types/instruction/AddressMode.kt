package io.github.josephsimutis.types.instruction

import io.github.josephsimutis.Virtual6502
import io.github.josephsimutis.types.RawShort
import io.github.josephsimutis.types.RawByte

enum class AddressMode(val instructionLength: UInt, val getAddress: (Virtual6502, RawShort) -> RawShort) {
    IMPLIED(1u, { _, _ -> RawShort.ZERO }),
    IMMEDIATE(2u, { v6502, _ -> v6502.PC + 1u }),
    ABSOLUTE(3u, { _, nextWord -> nextWord }),
    ABSOLUTE_X(3u, { v6502, nextWord -> nextWord + v6502.X }),
    ABSOLUTE_Y(3u, { v6502, nextWord -> nextWord + v6502.Y }),
    ZERO_PAGE(2u, { _, nextWord -> nextWord.low.toRawShort() }),

    // Note: ZERO_PAGE_X and ZERO_PAGE_Y are programmed in a bit of a weird way in order to ensure that the address always remains in the zero page.
    ZERO_PAGE_X(2u, { v6502, nextWord -> (nextWord.low + v6502.X).low.toRawShort() }),
    ZERO_PAGE_Y(2u, { v6502, nextWord -> (nextWord.low + v6502.Y).low.toRawShort() }),
    INDIRECT(3u, { v6502, nextWord -> v6502.readMemory(nextWord, nextWord + 1u) }),
    PRE_INDIRECT_X(3u, { v6502, nextWord -> v6502.readMemory(nextWord + v6502.X, nextWord + v6502.X + 1u) }),
    PRE_INDIRECT_Y(3u, { v6502, nextWord -> v6502.readMemory(nextWord + v6502.Y, nextWord + v6502.Y + 1u) }),
    POST_INDIRECT_X(3u, { v6502, nextWord -> v6502.readMemory(nextWord, nextWord + 1u) + v6502.X }),
    POST_INDIRECT_Y(3u, { v6502, nextWord -> v6502.readMemory(nextWord, nextWord + 1u) + v6502.Y }),
    RELATIVE(
        2u,
        { v6502, nextWord -> TODO("Unsure how exactly the branch instructions will work, wait until those are done.") })
}