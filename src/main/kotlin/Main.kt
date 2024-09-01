package io.github.josephsimutis

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.mordant.terminal.ConversionResult

class Shell6502() : CliktCommand() {
    val programMode by option(help="The mode the emulator uses.").switch(
        "--script" to ProgramMode.SCRIPT,
        "--interactive" to ProgramMode.INTERACTIVE
    ).required()
    val scriptFile by option(help="The script that is run when in script mode.").file(mustExist = true, canBeDir = false)
    private val languageString by option(help="The language that is used.").choice("assembly", "machine-code")
    val v6502 = Virtual6502()

    override fun run() {
        val language = when (languageString) {
            "assembly" -> Language.ASSEMBLY
            "machine-code" -> Language.MACHINE_CODE
            else -> throw Exception("Language must be either assembly or machine-code!")
        }
        v6502.on()
        when (programMode) {
            ProgramMode.SCRIPT -> {}
            ProgramMode.INTERACTIVE -> {
                var exiting = false
                while (!exiting) {
                    terminal.prompt(">", promptSuffix = " ") {
                        if (it == "exit") {
                            exiting = true
                        }
                        return@prompt ConversionResult.Valid(it)
                    }
                }
            }
        }
        v6502.off()
    }
}

fun main(args: Array<String>) = Shell6502().versionOption("v1.0.0-alpha.1").main(args)