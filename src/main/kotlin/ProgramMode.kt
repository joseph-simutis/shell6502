package io.github.josephsimutis

// Script mode uses an assembly program, compiles it, loads into virtual memory, and runs it.
// Interactive mode acts more like the python shell, where it writes and then runs the commands as they are entered by the user.
enum class ProgramMode {
    SCRIPT,
    INTERACTIVE
}