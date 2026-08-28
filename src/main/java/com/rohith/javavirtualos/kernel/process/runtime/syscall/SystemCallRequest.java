package com.rohith.javavirtualos.kernel.process.runtime.syscall;

public class SystemCallRequest {
    private final int syscallId;
    private final int arg1;
    private final int arg2;

    public SystemCallRequest(int syscallId, int arg1, int arg2) {
        this.syscallId = syscallId;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public int getSyscallId() { return syscallId; }
    public int getArg1() { return arg1; }
    public int getArg2() { return arg2; }
}
