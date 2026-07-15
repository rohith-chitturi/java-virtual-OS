package com.rohith.javavirtualos.shell;

import com.rohith.javavirtualos.command.Command;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores and resolves available shell commands.
 */
public class CommandRegistry {
    
    private final Map<String, Command> commands;

    public CommandRegistry() {
        this.commands = new HashMap<>();
    }

    public void register(Command command, String... aliases) {
        commands.put(command.getName().toLowerCase(), command);
        for (String alias : aliases) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public Collection<Command> getAllCommands() {
        return commands.values();
    }
}
