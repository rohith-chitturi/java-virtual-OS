package com.rohith.javavirtualos.kernel.process.runtime.syscall;

public class SystemCallResult {
    private final int returnValue;
    private final boolean error;

    public SystemCallResult(int returnValue, boolean error) {
        this.returnValue = returnValue;
        this.error = error;
    }

    public static SystemCallResult success(int returnValue) {
        return new SystemCallResult(returnValue, false);
    }
    
    public static SystemCallResult error(int errorCode) {
        return new SystemCallResult(errorCode, true);
    }

    public int getReturnValue() { return returnValue; }
    public boolean isError() { return error; }
}
