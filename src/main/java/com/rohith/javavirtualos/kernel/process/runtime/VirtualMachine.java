package com.rohith.javavirtualos.kernel.process.runtime;

import com.rohith.javavirtualos.kernel.process.runtime.syscall.SystemCallRequest;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SystemCallResult;
import com.rohith.javavirtualos.kernel.exceptions.IllegalInstructionException;

/**
 * The core CPU emulator for the custom instruction set.
 * Executes exactly ONE instruction per step().
 */
public class VirtualMachine {
    
    private final ExecutionContext context;
    private final Executable executable;
    private final RuntimeStatistics stats;
    
    private boolean halted = false;
    private int exitCode = 0;
    private int instructionCount = 0;
    private String faultState = null;

    public VirtualMachine(ExecutionContext context, Executable executable, RuntimeStatistics stats) {
        this.context = context;
        this.executable = executable;
        this.stats = stats;
    }

    public VirtualMachine(ExecutionContext context, Executable executable) {
        this(context, executable, null);
    }

    /**
     * Executes exactly ONE instruction and returns the ExecutionResult.
     */
    public ExecutionResult step() {
        if (halted) return ExecutionResult.HALT();
        if (context.getPc() >= executable.getInstructions().size()) {
            halted = true;
            return ExecutionResult.EXIT(0);
        }

        try {
            Instruction instr = executable.getInstructions().get(context.getPc());
            ExecutionResult result = executeInstruction(instr);
            
            if (stats != null) stats.addInstructionsExecuted(1);
            
            if (!isJumpInstruction(instr.getOpcode()) && result.getType() != ExecutionResult.Type.SYSCALL) {
                context.incrementPc();
            }
            
            instructionCount++;
            return result;
        } catch (Exception e) {
            halted = true;
            faultState = e.getMessage();
            if (stats != null) stats.incrementRuntimeFaults();
            return ExecutionResult.FAULT(faultState);
        }
    }

    private ExecutionResult executeInstruction(Instruction instr) {
        switch (instr.getOpcode()) {
            case LOAD: {
                int regLoad = parseRegister(instr.getOperand(0));
                int valLoad = parseInt(instr.getOperand(1));
                context.setRegister(regLoad, valLoad);
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case MOV: {
                int regDest = parseRegister(instr.getOperand(0));
                int regSrc = parseRegister(instr.getOperand(1));
                context.setRegister(regDest, context.getRegister(regSrc));
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case ADD: {
                int addDest = parseRegister(instr.getOperand(0));
                int addSrc = parseRegister(instr.getOperand(1));
                context.setRegister(addDest, context.getRegister(addDest) + context.getRegister(addSrc));
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case SUB: {
                int subDest = parseRegister(instr.getOperand(0));
                int subSrc = parseRegister(instr.getOperand(1));
                context.setRegister(subDest, context.getRegister(subDest) - context.getRegister(subSrc));
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case MUL: {
                int mulDest = parseRegister(instr.getOperand(0));
                int mulSrc = parseRegister(instr.getOperand(1));
                context.setRegister(mulDest, context.getRegister(mulDest) * context.getRegister(mulSrc));
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case DIV: {
                int divDest = parseRegister(instr.getOperand(0));
                int divSrc = parseRegister(instr.getOperand(1));
                int den = context.getRegister(divSrc);
                if (den == 0) throw new ArithmeticException("Division by zero");
                context.setRegister(divDest, context.getRegister(divDest) / den);
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case INC: {
                int incReg = parseRegister(instr.getOperand(0));
                context.setRegister(incReg, context.getRegister(incReg) + 1);
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case DEC: {
                int decReg = parseRegister(instr.getOperand(0));
                context.setRegister(decReg, context.getRegister(decReg) - 1);
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case CMP: {
                int cmpA = context.getRegister(parseRegister(instr.getOperand(0)));
                int cmpB = context.getRegister(parseRegister(instr.getOperand(1)));
                context.setZeroFlag(cmpA == cmpB);
                context.setNegativeFlag(cmpA < cmpB);
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case JMP: {
                context.setPc(parseInt(instr.getOperand(0)));
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case JZ: {
                if (context.isZeroFlag()) {
                    context.setPc(parseInt(instr.getOperand(0)));
                } else {
                    context.incrementPc();
                }
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case JNZ: {
                if (!context.isZeroFlag()) {
                    context.setPc(parseInt(instr.getOperand(0)));
                } else {
                    context.incrementPc();
                }
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case CALL: {
                context.pushCall(context.getPc() + 1);
                context.setPc(parseInt(instr.getOperand(0)));
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case RETURN: {
                context.setPc(context.popCall());
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case NOP: {
                return ExecutionResult.INSTRUCTION_EXECUTED();
            }
            case PRINT: {
                // Synthesize a write syscall to FD 1 (stdout)
                // We'll pass a special syscall ID for PRINT, or just use WRITE.
                // According to requirements: "Prefer routing PRINT through the same abstraction used by SYS_WRITE"
                // However, PRINT takes a register or string. Let's pass it via a special SYS_PRINT for simplicity,
                // and the handler will use VirtualOutput.
                return ExecutionResult.SYSCALL(new SystemCallRequest(2, parseOperand(instr.getOperand(0)), 0)); 
            }
            case SLEEP: {
                int duration = parseInt(instr.getOperand(0));
                return ExecutionResult.SLEEP(duration);
            }
            case YIELD: {
                return ExecutionResult.YIELD();
            }
            case SYSCALL: {
                if (stats != null) stats.incrementSystemCallsInvoked();
                // SYSCALL ID_REG, ARG1_REG, ARG2_REG
                // or SYSCALL ID_VAL
                int syscallId = parseOperand(instr.getOperand(0));
                int arg1 = 0;
                int arg2 = 0;
                if (instr.getOperands().length > 1) {
                    arg1 = parseOperand(instr.getOperand(1));
                }
                if (instr.getOperands().length > 2) {
                    arg2 = parseOperand(instr.getOperand(2));
                }
                
                if (syscallId == 1) { // 1 = EXIT
                    this.exitCode = arg1;
                    this.halted = true;
                    return ExecutionResult.EXIT(arg1);
                }
                
                return ExecutionResult.SYSCALL(new SystemCallRequest(syscallId, arg1, arg2));
            }
            case EXIT: {
                this.exitCode = parseOperand(instr.getOperand(0));
                this.halted = true;
                return ExecutionResult.EXIT(this.exitCode);
            }
            case HALT: {
                this.halted = true;
                return ExecutionResult.HALT();
            }
            default:
                throw new IllegalInstructionException("Opcode " + instr.getOpcode() + " not implemented in VM");
        }
    }
    
    /**
     * Called by the Dispatcher when a SYSCALL completes asynchronously.
     */
    public void setSystemCallResult(SystemCallResult result) {
        context.setRegister(0, result.getReturnValue());
        context.incrementPc();
    }

    private boolean isJumpInstruction(Opcode opcode) {
        return opcode == Opcode.JMP || 
               opcode == Opcode.JZ || opcode == Opcode.JNZ || 
               opcode == Opcode.CALL || opcode == Opcode.RETURN;
    }

    private int parseRegister(String regStr) {
        if (regStr.startsWith("R") || regStr.startsWith("r")) {
            return Integer.parseInt(regStr.substring(1));
        }
        throw new IllegalArgumentException("Invalid register format: " + regStr);
    }
    
    private int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer format: " + str);
        }
    }
    
    private int parseOperand(String operand) {
        if (operand.startsWith("R") || operand.startsWith("r")) {
            return context.getRegister(parseRegister(operand));
        } else {
            return parseInt(operand);
        }
    }
    
    public int getExitCode() { return exitCode; }
    public boolean isHalted() { return halted; }
    public int getInstructionCount() { return instructionCount; }
    public String getFaultState() { return faultState; }
    public ExecutionContext getContext() { return context; }
}

