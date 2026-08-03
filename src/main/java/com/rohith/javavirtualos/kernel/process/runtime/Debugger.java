package com.rohith.javavirtualos.kernel.process.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * Placeholder for the Virtual Machine Debugger architecture.
 */
public class Debugger {
    
    private final VirtualMachine vm;
    private final List<Breakpoint> breakpoints;
    
    public Debugger(VirtualMachine vm) {
        this.vm = vm;
        this.breakpoints = new ArrayList<>();
    }
    
    public void addBreakpoint(Breakpoint bp) {
        breakpoints.add(bp);
    }
    
    public void step() {
        // Future implementation: execute single instruction
    }
    
    public void dumpRegisters() {
        // Future implementation: print R0-R3, PC, SP
    }
}
