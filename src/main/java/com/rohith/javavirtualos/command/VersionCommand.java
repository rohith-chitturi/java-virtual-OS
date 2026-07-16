package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;

public class VersionCommand implements Command {
    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return CommandResult.success(context.getSystemContext().getOsName() + " v" + context.getSystemContext().getOsVersion());
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Print OS version information";
    }
}
