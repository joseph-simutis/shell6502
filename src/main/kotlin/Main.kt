package io.github.josephsimutis

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.mordant.terminal.ConversionResult
import io.github.josephsimutis.types.RawByte
import io.github.josephsimutis.types.RawShort

class Shell6502() : CliktCommand() {
    val programMode by option(help = "The mode the emulator uses.").switch(
        "--script" to ProgramMode.SCRIPT,
        "--interactive" to ProgramMode.INTERACTIVE
    ).required()
    val scriptFile by option(
        help = """
        The script that is run when in script mode.
        Only required in script mode.
        
    """.trimIndent()
    ).file(mustExist = true, canBeDir = false)
    val v6502 = Virtual6502()

    @OptIn(ExperimentalStdlibApi::class)
    override fun run() {
        v6502.on()
        when (programMode) {
            ProgramMode.SCRIPT -> {}
            ProgramMode.INTERACTIVE -> {
                echo("Welcome to the interactive 6502 shell! Type @help for help.")
                var exiting = false
                while (!exiting) {
                    terminal.prompt("> ", promptSuffix = "") { command ->
                        if (command.startsWith('@')) {
                            when (command) {
                                "@print" -> {
                                    terminal.prompt("Enter the address in the form \"LLHH\"") {
                                        try {
                                            if (it.length != 4) throw IllegalArgumentException()
                                            val bytes = it.chunked(2)
                                            val address = RawShort(RawByte(bytes[0].hexToUByte()), RawByte(bytes[1].hexToUByte()))
                                            echo("The value at address $address is ${v6502.readMemory(address)}")
                                            ConversionResult.Valid(it)
                                        } catch (_: IllegalArgumentException) {
                                            ConversionResult.Invalid("$it is not a valid address!")
                                        }
                                    }
                                }
                                "@help" -> echo(
                                    """
                                    Emulator Commands:
                                    
                                    @print: Asks the user for a location in the V6502's memory, and then prints the contents of that location.
                                    @help: Shows this message.
                                    @exit: Exits the program.
                                    
                                    To run an instruction on the V6502, simply type the assembly for it in without starting with a @.
                                """.trimIndent()
                                )

                                "@exit" -> exiting = true
                                else -> echo("Invalid command! Type @help for help.")
                            }
                        }
                        ConversionResult.Valid(command)
                    }
                }
            }
        }
        v6502.off()
    }
}

fun main(args: Array<String>) = Shell6502().versionOption("v1.0.0-pre.1").main(args)