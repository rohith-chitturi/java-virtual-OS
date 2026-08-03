package com.rohith.javavirtualos.kernel.process.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses raw text (.vexe files) into a List of Instructions for the VM.
 */
public class ExecutableLoader {

    public static List<Instruction> parse(List<String> sourceCode) {
        List<Instruction> instructions = new ArrayList<>();
        
        for (String line : sourceCode) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                continue; // Skip comments and empty lines
            }
            
            String[] parts = line.split("\\s+");
            String opcodeStr = parts[0].toUpperCase();
            
            Opcode opcode;
            try {
                opcode = Opcode.valueOf(opcodeStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown instruction: " + opcodeStr);
            }
            
            String[] operands = new String[parts.length - 1];
            for (int i = 1; i < parts.length; i++) {
                // Remove trailing commas from operands (e.g. LOAD R1, 10 -> LOAD R1 10)
                operands[i-1] = parts[i].replace(",", "");
            }
            
            instructions.add(new Instruction(opcode, operands));
        }
        
        return instructions;
    }
}
