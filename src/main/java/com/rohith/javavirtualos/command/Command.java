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
     * @return the name of the command
     */
    String getName();

    /**
     * @return a short description of what the command does
     */
    String getDescription();
}
