package com.rohith.javavirtualos.kernel.process.runtime;



/**
 * Represents a single parsed instruction for the Virtual Machine.
 */
public class Instruction {
    private final Opcode opcode;
    private final String[] operands;

    public Instruction(Opcode opcode, String... operands) {
        this.opcode = opcode;
        this.operands = operands != null ? operands : new String[0];
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public String[] getOperands() {
        return operands;
    }

    public String getOperand(int index) {
        if (index >= 0 && index < operands.length) {
            return operands[index];
        }
        return null;
    }

    @Override
    public String toString() {
        return opcode.name() + " " + String.join(" ", operands);
    }
}
