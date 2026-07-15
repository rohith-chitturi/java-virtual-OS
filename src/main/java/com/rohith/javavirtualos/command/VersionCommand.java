package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;

public class VersionCommand implements Command {
    @Override
    public boolean execute(String[] args, ShellContext context) {
        context.getOut().println(context.getSystemContext().getOsName() + " v" + context.getSystemContext().getOsVersion());
        return true;
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
