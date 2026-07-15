package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.CommandRegistry;
import com.rohith.javavirtualos.shell.ShellContext;

public class HelpCommand implements Command {
    
    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean execute(String[] args, ShellContext context) {
        context.getOut().println("Available Commands:");
        for (Command cmd : registry.getAllCommands()) {
            context.getOut().printf("  %-10s - %s%n", cmd.getName(), cmd.getDescription());
        }
        return true;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Print available commands and their descriptions";
    }
}
