package com.rohith.javavirtualos.command;

/**
 * Standardized response for all commands, decoupling them from direct console output.
 */
public class CommandResult {
    
    private final boolean success;
    private final String message;
    private final Object payload;
    private final boolean terminateShell;

    public CommandResult(boolean success, String message, Object payload, boolean terminateShell) {
        this.success = success;
        this.message = message;
        this.payload = payload;
        this.terminateShell = terminateShell;
    }

    public static CommandResult success() {
        return new CommandResult(true, null, null, false);
    }

    public static CommandResult success(String message) {
        return new CommandResult(true, message, null, false);
    }

    public static CommandResult failure(String message) {
        return new CommandResult(false, message, null, false);
    }

    public static CommandResult terminate(String message) {
        return new CommandResult(true, message, null, true);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getPayload() {
        return payload;
    }

    public boolean shouldTerminateShell() {
        return terminateShell;
    }
}
