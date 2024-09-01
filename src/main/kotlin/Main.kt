package io.github.josephsimutis

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.switch
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.file

class Shell6502() : CliktCommand() {
    val programMode by option(help="The mode the emulator uses.").switch(
        "--script" to ProgramMode.SCRIPT,
        "--interactive" to ProgramMode.INTERACTIVE
    ).default(ProgramMode.INTERACTIVE)
    val script by option().file(mustExist = true, canBeDir = false)
    val v6502 = Virtual6502()

    override fun run() {
        v6502.initialize()
        v6502.clockCycle()
    }
}

fun main(args: Array<String>) = Shell6502().versionOption("DEV").main(args)