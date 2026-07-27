package com.rohith.javavirtualos.kernel.events;

public abstract class IpcEvent extends KernelEvent {
    public abstract String getMessage();

    public static class SignalSentEvent extends IpcEvent {
        private final int sourcePid;
        private final int targetPid;
        private final String signalName;
        public SignalSentEvent(int sourcePid, int targetPid, String signalName) {
            this.sourcePid = sourcePid; this.targetPid = targetPid; this.signalName = signalName;
        }
        @Override public String getMessage() { return "Signal " + signalName + " sent from PID " + sourcePid + " to PID " + targetPid; }
    }

    public static class PipeCreatedEvent extends IpcEvent {
        private final long pipeId;
        public PipeCreatedEvent(long pipeId) { this.pipeId = pipeId; }
        @Override public String getMessage() { return "Pipe created: " + pipeId; }
    }
    
    public static class SharedMemoryCreatedEvent extends IpcEvent {
        private final long shmId;
        public SharedMemoryCreatedEvent(long shmId) { this.shmId = shmId; }
        @Override public String getMessage() { return "Shared Memory created: " + shmId; }
    }
}
