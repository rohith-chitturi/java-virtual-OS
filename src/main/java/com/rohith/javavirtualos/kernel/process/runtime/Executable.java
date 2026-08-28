package com.rohith.javavirtualos.kernel.process.runtime;

import java.util.List;

/**
 * Represents a parsed executable program.
 */
public class Executable {
    private final String name;
    private final int version;
    private final List<Instruction> instructions;

    public Executable(String name, int version, List<Instruction> instructions) {
        this.name = name;
        this.version = version;
        this.instructions = instructions;
    }

    public String getName() { return name; }
    public int getVersion() { return version; }
    public List<Instruction> getInstructions() { return instructions; }
}
