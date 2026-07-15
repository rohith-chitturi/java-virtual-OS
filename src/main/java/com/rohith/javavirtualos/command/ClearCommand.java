package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;

public class ClearCommand implements Command {
    @Override
    public boolean execute(String[] args, ShellContext context) {
        // Simple cross-platform terminal clear using ANSI escape sequences
        context.getOut().print("\033[H\033[2J");
        context.getOut().flush();
        return true;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clear the terminal screen";
    }
}
