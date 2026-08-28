package com.rohith.javavirtualos.kernel.process.runtime;

import com.rohith.javavirtualos.kernel.exceptions.ExecutableFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses raw text (.vexe files) into an Executable for the VM.
 */
public class ExecutableLoader {

    public static Executable parse(List<String> sourceCode) {
        String programName = "unknown";
        int version = 1;
        
        List<String> validLines = new ArrayList<>();
        Map<String, Integer> labels = new HashMap<>();
        
        // Pass 1: Extract metadata, collect valid instructions, map labels
        int instructionCount = 0;
        for (String line : sourceCode) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                continue;
            }
            
            if (line.startsWith("PROGRAM ")) {
                programName = line.substring(8).trim();
                continue;
            }
            if (line.startsWith("VERSION ")) {
                try {
                    version = Integer.parseInt(line.substring(8).trim());
                } catch (NumberFormatException e) {
                    throw new ExecutableFormatException("Invalid VERSION format");
                }
                continue;
            }
            
            if (line.endsWith(":")) {
                String label = line.substring(0, line.length() - 1).trim();
                labels.put(label, instructionCount);
                continue; // Labels don't count as instructions
            }
            
            validLines.add(line);
            instructionCount++;
        }
        
        // Pass 2: Parse instructions
        List<Instruction> instructions = new ArrayList<>();
        Pattern stringPattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        
        for (int pc = 0; pc < validLines.size(); pc++) {
            String line = validLines.get(pc);
            
            List<String> tokens = new ArrayList<>();
            Matcher matcher = stringPattern.matcher(line);
            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    tokens.add("\"" + matcher.group(1) + "\""); // keep quotes for string literal
                } else {
                    tokens.add(matcher.group(2).replace(",", "")); // remove commas from registers/numbers
                }
            }
            
            if (tokens.isEmpty()) continue;
            
            String opcodeStr = tokens.get(0).toUpperCase();
            Opcode opcode;
            try {
                opcode = Opcode.valueOf(opcodeStr);
            } catch (IllegalArgumentException e) {
                throw new ExecutableFormatException("Unknown instruction: " + opcodeStr + " at PC=" + pc);
            }
            
            String[] operands = new String[tokens.size() - 1];
            for (int i = 1; i < tokens.size(); i++) {
                String operand = tokens.get(i);
                // Resolve label if present
                if (labels.containsKey(operand)) {
                    operand = String.valueOf(labels.get(operand));
                }
                operands[i-1] = operand;
            }
            
            instructions.add(new Instruction(opcode, operands));
        }
        
        return new Executable(programName, version, instructions);
    }
}

