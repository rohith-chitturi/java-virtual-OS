package com.rohith.javavirtualos.kernel.process.runtime;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VirtualMachineTest {

    @Test
    void testBasicArithmeticAndRegisters() {
        List<String> source = Arrays.asList(
            "LOAD R0 10",
            "LOAD R1 20",
            "ADD R0 R1",   // R0 = 10 + 20 = 30
            "MOV R2 R0",   // R2 = 30
            "SUB R2 R1",   // R2 = 30 - 20 = 10
            "EXIT"
        );
        
        List<Instruction> instructions = ExecutableLoader.parse(source);
        ExecutionContext ctx = new ExecutionContext(1);
        SystemCallInterface sys = new SystemCallInterface(null, null);
        VirtualMachine vm = new VirtualMachine(ctx, instructions, sys);
        
        vm.run();
        
        assertEquals(30, ctx.getRegister(0));
        assertEquals(20, ctx.getRegister(1));
        assertEquals(10, ctx.getRegister(2));
    }

    @Test
    void testJumpAndLoop() {
        List<String> source = Arrays.asList(
            "LOAD R0 0",    // Counter
            "LOAD R1 5",    // Max
            "LOAD R2 1",    // Increment
            
            // Loop start (PC=3)
            "CMP R0 R1",
            "JEQ 8",        // If R0 == R1 (5), jump to EXIT (PC=8)
            "ADD R0 R2",
            "JMP 3",        // Jump back to CMP
            
            // Exit (PC=8)
            "EXIT"
        );
        
        List<Instruction> instructions = ExecutableLoader.parse(source);
        ExecutionContext ctx = new ExecutionContext(2);
        SystemCallInterface sys = new SystemCallInterface(null, null);
        VirtualMachine vm = new VirtualMachine(ctx, instructions, sys);
        
        vm.run();
        
        assertEquals(5, ctx.getRegister(0));
        assertEquals(5, ctx.getRegister(1));
    }
}
