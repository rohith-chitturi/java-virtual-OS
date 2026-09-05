package com.rohith.javavirtualos.kernel.process.runtime.syscall;

public class SystemCallRequest {
    private final int syscallId;
    private final int arg1;
    private final int arg2;
    private final String stringArg;

    public SystemCallRequest(int syscallId, int arg1, int arg2, String stringArg) {
        this.syscallId = syscallId;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.stringArg = stringArg;
    }

    public SystemCallRequest(int syscallId, int arg1, int arg2) {
        this(syscallId, arg1, arg2, null);
    }

    public int getSyscallId() { return syscallId; }
    public int getArg1() { return arg1; }
    public int getArg2() { return arg2; }
    public String getStringArg() { return stringArg; }
}
