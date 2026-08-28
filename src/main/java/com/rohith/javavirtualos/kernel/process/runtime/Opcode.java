package com.rohith.javavirtualos.kernel.process.runtime;

/**
 * Defines the Virtual OS Assembly Language opcodes.
 */
public enum Opcode {
    LOAD,     
    STORE,    
    MOV,      
    ADD,      
    SUB,      
    MUL,
    DIV,
    INC,
    DEC,
    CMP,      
    JMP,      
    JZ,       
    JNZ,      
    CALL,     
    RETURN,   
    NOP,
    PRINT,    
    SLEEP,    
    YIELD,    
    SYSCALL,  
    EXIT,
    HALT
}

