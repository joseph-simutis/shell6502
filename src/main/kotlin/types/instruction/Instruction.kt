package io.github.josephsimutis.types.instruction

import io.github.josephsimutis.Virtual6502
import io.github.josephsimutis.types.RawShort

enum class Instruction(val run: (Virtual6502, RawShort) -> Unit) {}