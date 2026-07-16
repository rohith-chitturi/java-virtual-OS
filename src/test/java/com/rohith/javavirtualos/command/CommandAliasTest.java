package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.CommandRegistry;
import com.rohith.javavirtualos.shell.ShellContext;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandAliasTest {

    @Test
    public void testCommandAliases() {
        CommandRegistry registry = new CommandRegistry();
        Command testCommand = new Command() {
            @Override
            public CommandResult execute(String[] args, ShellContext context) {
                return CommandResult.success();
            }

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getDescription() {
                return "test";
            }
        };

        registry.register(testCommand, "alias1", "alias2");

        assertNotNull(registry.getCommand("test"));
        assertNotNull(registry.getCommand("alias1"));
        assertNotNull(registry.getCommand("alias2"));
        
        assertEquals(testCommand, registry.getCommand("alias1"));
        assertEquals(testCommand, registry.getCommand("alias2"));
    }
}
