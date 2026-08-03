package com.rohith.javavirtualos.kernel.process.runtime;

import java.util.List;

/**
 * The core CPU emulator for the custom instruction set.
 * Executes instructions sequentially and interacts with the SystemCallInterface.
 */
public class VirtualMachine implements Runnable {
    
    private final ExecutionContext context;
    private final List<Instruction> programMemory;
    private final SystemCallInterface syscallInterface;
    
    private boolean isRunning;
    private int exitCode = 0;

    public VirtualMachine(ExecutionContext context, List<Instruction> programMemory, SystemCallInterface syscallInterface) {
        this.context = context;
        this.programMemory = programMemory;
        this.syscallInterface = syscallInterface;
        this.isRunning = false;
    }

    /**
     * Runs the virtual machine until it exits or encounters an error.
     */
    public void run() {
        isRunning = true;
        
        while (isRunning && context.getPc() < programMemory.size()) {
            Instruction instr = programMemory.get(context.getPc());
            executeInstruction(instr);
            
            // If it's a jump, PC is already updated. Otherwise, increment.
            if (!isJumpInstruction(instr.getOpcode())) {
                context.incrementPc();
            }
        }
    }

    private void executeInstruction(Instruction instr) {
        switch (instr.getOpcode()) {
            case LOAD:
                int regLoad = parseRegister(instr.getOperand(0));
                int valLoad = Integer.parseInt(instr.getOperand(1));
                context.setRegister(regLoad, valLoad);
                break;
            case MOV:
                int regDest = parseRegister(instr.getOperand(0));
                int regSrc = parseRegister(instr.getOperand(1));
                context.setRegister(regDest, context.getRegister(regSrc));
                break;
            case ADD:
                int addDest = parseRegister(instr.getOperand(0));
                int addSrc = parseRegister(instr.getOperand(1));
                context.setRegister(addDest, context.getRegister(addDest) + context.getRegister(addSrc));
                break;
            case SUB:
                int subDest = parseRegister(instr.getOperand(0));
                int subSrc = parseRegister(instr.getOperand(1));
                context.setRegister(subDest, context.getRegister(subDest) - context.getRegister(subSrc));
                break;
            case CMP:
                int cmpA = context.getRegister(parseRegister(instr.getOperand(0)));
                int cmpB = context.getRegister(parseRegister(instr.getOperand(1)));
                context.setZeroFlag(cmpA == cmpB);
                context.setNegativeFlag(cmpA < cmpB);
                break;
            case JMP:
                context.setPc(Integer.parseInt(instr.getOperand(0)));
                break;
            case JEQ:
                if (context.isZeroFlag()) {
                    context.setPc(Integer.parseInt(instr.getOperand(0)));
                } else {
                    context.incrementPc();
                }
                break;
            case JNE:
                if (!context.isZeroFlag()) {
                    context.setPc(Integer.parseInt(instr.getOperand(0)));
                } else {
                    context.incrementPc();
                }
                break;
            case PRINT:
                int printReg = parseRegister(instr.getOperand(0));
                // Technically we could use SYSCALL for this, but PRINT is a convenient debug instruction
                System.out.println(context.getRegister(printReg));
                break;
            case SLEEP:
                try {
                    Thread.sleep(Integer.parseInt(instr.getOperand(0)));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
            case YIELD:
                Thread.yield();
                break;
            case SYSCALL:
                int syscallId = Integer.parseInt(instr.getOperand(0));
                int arg1 = 0;
                int arg2 = 0;
                if (instr.getOperands().length > 1) {
                    arg1 = context.getRegister(parseRegister(instr.getOperand(1)));
                }
                if (instr.getOperands().length > 2) {
                    arg2 = context.getRegister(parseRegister(instr.getOperand(2)));
                }
                
                int result = syscallInterface.handleSyscall(context, syscallId, arg1, arg2);
                context.setRegister(0, result); // Typically store syscall result in R0
                
                if (syscallId == 1) { // 1 = EXIT
                    this.exitCode = result;
                    isRunning = false;
                }
                break;
            case EXIT:
                this.exitCode = context.getRegister(0);
                isRunning = false;
                break;
            default:
                throw new UnsupportedOperationException("Opcode " + instr.getOpcode() + " not implemented in VM");
        }
    }

    private boolean isJumpInstruction(Opcode opcode) {
        return opcode == Opcode.JMP || opcode == Opcode.JEQ || opcode == Opcode.JNE || opcode == Opcode.CALL || opcode == Opcode.RETURN;
    }

    private int parseRegister(String regStr) {
        if (regStr.startsWith("R") || regStr.startsWith("r")) {
            return Integer.parseInt(regStr.substring(1));
        }
        throw new IllegalArgumentException("Invalid register format: " + regStr);
    }
    
    public int getExitCode() {
        return exitCode;
    }
}
