package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.CommandRegistry;
import com.rohith.javavirtualos.shell.ShellContext;

public class HelpCommand implements Command {
    
    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        StringBuilder sb = new StringBuilder("Available Commands:\n");
        for (Command cmd : registry.getAllCommands()) {
            sb.append(String.format("  %-10s - %s%n", cmd.getName(), cmd.getDescription()));
        }
        return CommandResult.success(sb.toString().trim());
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
