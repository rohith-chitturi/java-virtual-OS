package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;

public class EchoCommand implements Command {
    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return CommandResult.success(String.join(" ", args));
    }

    @Override
    public String getName() {
        return "echo";
    }

    @Override
    public String getDescription() {
        return "Prints the given arguments to the terminal";
    }
}
