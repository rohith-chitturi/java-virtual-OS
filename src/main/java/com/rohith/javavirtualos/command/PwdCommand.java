package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;

public class PwdCommand implements Command {
    @Override
    public boolean execute(String[] args, ShellContext context) {
        context.getOut().println(context.getCurrentDirectory());
        return true;
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
