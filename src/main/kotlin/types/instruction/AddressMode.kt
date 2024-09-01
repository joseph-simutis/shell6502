package io.github.josephsimutis.types.instruction

import io.github.josephsimutis.Virtual6502
import io.github.josephsimutis.types.RawShort

enum class AddressMode(val instructionLength: UInt, val getAddress: (Virtual6502, RawShort) -> RawShort)