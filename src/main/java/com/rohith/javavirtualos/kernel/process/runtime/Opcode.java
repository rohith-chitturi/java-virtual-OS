package com.rohith.javavirtualos.kernel.process.runtime;

/**
 * Defines the Virtual OS Assembly Language opcodes.
 */
public enum Opcode {
    LOAD,     // LOAD R1, 10
    STORE,    // STORE R1, 0x100
    MOV,      // MOV R1, R2
    ADD,      // ADD R1, R2
    SUB,      // SUB R1, R2
    CMP,      // CMP R1, R2 (sets flags)
    JMP,      // JMP 100
    JEQ,      // JEQ 100 (Jump if Equal)
    JNE,      // JNE 100 (Jump if Not Equal)
    CALL,     // CALL 200
    RETURN,   // RETURN
    PRINT,    // PRINT R1 (prints register value to stdout)
    SLEEP,    // SLEEP 1000
    YIELD,    // YIELD (returns control to scheduler)
    SYSCALL,  // SYSCALL 1 (e.g. 1=EXIT)
    EXIT      // EXIT (terminates VM)
}
