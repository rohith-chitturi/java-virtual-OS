package com.rohith.javavirtualos.kernel.process.runtime;

/**
 * Placeholder for the Virtual Machine Breakpoint architecture.
 */
public class Breakpoint {
    
    private final int instructionIndex;
    
    public Breakpoint(int instructionIndex) {
        this.instructionIndex = instructionIndex;
    }
    
    public int getInstructionIndex() {
        return instructionIndex;
    }
}
