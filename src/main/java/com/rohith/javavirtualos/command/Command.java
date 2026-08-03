package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;

/**
 * The Command interface implemented by all shell commands.
 */
public interface Command {
    
    /**
     * Executes the command.
     * @param args the arguments passed from the shell
     * @param context the shell execution context
     * @return a CommandResult containing success status and optional message
     */
    CommandResult execute(String[] args, ShellContext context);

    /**
     * Executes the command with virtual streams for I/O redirection.
     * By default, it delegates to the standard execute method.
     *
     * @param args the arguments passed from the shell
     * @param context the shell execution context
     * @param in the virtual input stream
     * @param out the virtual output stream
     * @return a CommandResult containing success status
     */
    default CommandResult execute(String[] args, ShellContext context, com.rohith.javavirtualos.shell.stream.VirtualInput in, com.rohith.javavirtualos.shell.stream.VirtualOutput out) {
        return execute(args, context);
    }

    /**
     * @return the name of the command
     */
    String getName();

    /**
     * @return a short description of what the command does
     */
    String getDescription();
}
