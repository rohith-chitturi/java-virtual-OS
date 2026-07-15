package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;

public class PwdCommand implements Command {
    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return CommandResult.success(context.getCurrentDirectory());
    }

    @Override
    public String getName() {
        return "pwd";
    }

    @Override
    public String getDescription() {
        return "Print current working directory";
    }
}
