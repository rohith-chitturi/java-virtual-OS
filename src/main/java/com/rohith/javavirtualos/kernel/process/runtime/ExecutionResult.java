package com.rohith.javavirtualos.kernel.process.runtime;

import com.rohith.javavirtualos.kernel.process.runtime.syscall.SystemCallRequest;

/**
 * Represents the immutable result of a single VM execution step.
 */
public class ExecutionResult {
    public enum Type {
        INSTRUCTION_EXECUTED,
        YIELD,
        SLEEP,
        SYSCALL,
        EXIT,
        HALT,
        FAULT
    }

    private final Type type;
    private final int value; // Used for sleep duration or exit code
    private final SystemCallRequest syscallRequest;
    private final String faultReason;

    private ExecutionResult(Type type, int value, SystemCallRequest syscallRequest, String faultReason) {
        this.type = type;
        this.value = value;
        this.syscallRequest = syscallRequest;
        this.faultReason = faultReason;
    }

    public static ExecutionResult INSTRUCTION_EXECUTED() {
        return new ExecutionResult(Type.INSTRUCTION_EXECUTED, 0, null, null);
    }

    public static ExecutionResult YIELD() {
        return new ExecutionResult(Type.YIELD, 0, null, null);
    }

    public static ExecutionResult SLEEP(int duration) {
        return new ExecutionResult(Type.SLEEP, duration, null, null);
    }

    public static ExecutionResult EXIT(int exitCode) {
        return new ExecutionResult(Type.EXIT, exitCode, null, null);
    }

    public static ExecutionResult HALT() {
        return new ExecutionResult(Type.HALT, 0, null, null);
    }

    public static ExecutionResult SYSCALL(SystemCallRequest request) {
        return new ExecutionResult(Type.SYSCALL, 0, request, null);
    }

    public static ExecutionResult FAULT(String reason) {
        return new ExecutionResult(Type.FAULT, 0, null, reason);
    }

    public Type getType() { return type; }
    public int getValue() { return value; }
    public SystemCallRequest getSyscallRequest() { return syscallRequest; }
    public String getFaultReason() { return faultReason; }
}
