package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;

public class ExitCommand implements Command {
    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return CommandResult.terminate("Goodbye!");
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Exit the virtual shell";
    }
}
