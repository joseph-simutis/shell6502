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
                                    terminal.prompt("Enter the low byte") { lowInput ->
                                        try {
                                            if (lowInput.length != 2) throw IllegalArgumentException()
                                            val low = RawByte(lowInput.hexToUByte())
                                            terminal.prompt("Enter the high byte") { highInput ->
                                                try {
                                                    if (highInput.length != 2) throw IllegalArgumentException()
                                                    val high = RawByte(highInput.toInt(16).toUByte())
                                                    val address = RawShort(low, high)
                                                    echo("The value stored at address $address is ${v6502.readMemory(address)}")
                                                    ConversionResult.Valid(highInput)
                                                } catch (_: IllegalArgumentException) {
                                                    ConversionResult.Invalid("$highInput is not a valid byte!")
                                                }
                                            }
                                            ConversionResult.Valid(lowInput)
                                        } catch (_: IllegalArgumentException) {
                                            ConversionResult.Invalid("$lowInput is not a valid byte!")
                                        }
                                    }
                                }
                                "@help" -> echo(
                                    """
                                    Emulator Commands:
                                    
                                    @print: Asks the user for a location in the V6502's memory, and then prints the contents of that location.
                                    @help: Shows this message.
                                    @exit: Exits the program.
                                    
                                    To run an instruction on the V6502, simply type the assembly for it in.
                                """.trimIndent()
                                )

                                "@exit" -> exiting = true
                                else -> echo("Invalid command! Type @help for help.", err=true)
                            }
                        } else {
                            if (Assembler.isAssembly(command)) {
                                var index = v6502.PC
                                Assembler.assemble(command)!!.forEach { byte ->
                                    v6502.writeMemory(index, byte)
                                    index++
                                }
                                v6502.clockCycle()
                            } else {
                                echo("Not assembly! Type @help for help.", err=true)
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