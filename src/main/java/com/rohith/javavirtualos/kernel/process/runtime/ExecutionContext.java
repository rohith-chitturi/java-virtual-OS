package com.rohith.javavirtualos.kernel.process.runtime;


import java.util.Stack;

/**
 * Encapsulates the runtime state of a Virtual Machine instance.
 */
public class ExecutionContext {
    // Registers R0-R3
    private final int[] registers = new int[4];
    
    // Special Registers
    private int pc = 0; // Program Counter
    private int sp = 0; // Stack Pointer
    
    // Flags (e.g. for CMP and JEQ)
    private boolean zeroFlag = false;
    private boolean negativeFlag = false;
    
    // Minimal Stack for CALL/RETURN
    private final Stack<Integer> callStack = new Stack<>();
    
    // References to underlying OS constructs
    private final int processId;

    public ExecutionContext(int processId) {
        this.processId = processId;
    }

    public int getRegister(int index) {
        if (index >= 0 && index < registers.length) return registers[index];
        throw new IllegalArgumentException("Invalid register index");
    }

    public void setRegister(int index, int value) {
        if (index >= 0 && index < registers.length) registers[index] = value;
        else throw new IllegalArgumentException("Invalid register index");
    }

    public int getPc() { return pc; }
    public void setPc(int pc) { this.pc = pc; }
    public void incrementPc() { this.pc++; }

    public int getSp() { return sp; }
    public void setSp(int sp) { this.sp = sp; }

    public boolean isZeroFlag() { return zeroFlag; }
    public void setZeroFlag(boolean zeroFlag) { this.zeroFlag = zeroFlag; }

    public boolean isNegativeFlag() { return negativeFlag; }
    public void setNegativeFlag(boolean negativeFlag) { this.negativeFlag = negativeFlag; }

    public void pushCall(int returnAddress) {
        callStack.push(returnAddress);
    }
    
    public int popCall() {
        if (callStack.isEmpty()) throw new IllegalStateException("Call stack underflow");
        return callStack.pop();
    }

    public int getProcessId() { return processId; }
}
