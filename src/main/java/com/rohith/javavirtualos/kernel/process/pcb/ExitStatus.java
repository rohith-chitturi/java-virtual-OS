package com.rohith.javavirtualos.kernel.process.pcb;

public class ExitStatus {
    private final int exitCode;
    private final Integer signal;
    private final boolean normalExit;

    private ExitStatus(int exitCode, Integer signal, boolean normalExit) {
        this.exitCode = exitCode;
        this.signal = signal;
        this.normalExit = normalExit;
    }

    public static ExitStatus normal(int exitCode) {
        return new ExitStatus(exitCode, null, true);
    }

    public static ExitStatus signaled(int signal) {
        return new ExitStatus(0, signal, false);
    }

    public int getExitCode() { return exitCode; }
    public Integer getSignal() { return signal; }
    public boolean isNormalExit() { return normalExit; }
    
    @Override
    public String toString() {
        if (normalExit) {
            return "Exited normally with code " + exitCode;
        } else {
            return "Killed by signal " + signal;
        }
    }
}
