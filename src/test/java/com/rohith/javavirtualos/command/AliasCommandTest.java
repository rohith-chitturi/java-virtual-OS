package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.SystemContext;
import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.shell.ShellContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class AliasCommandTest {

    private ShellContext context;
    private AliasCommand command;

    @BeforeEach
    public void setup() {
        context = new ShellContext(null, new User("root", "password"), new PrintStream(new ByteArrayOutputStream()), new ByteArrayInputStream(new byte[0]));
        command = new AliasCommand();
    }

    @Test
    public void testSetAndGetAlias() {
        command.execute(new String[]{"ll=ls -l"}, context, null, null);
        assertEquals("ls -l", context.getAliases().get("ll"));

        // Quotes should be stripped
        command.execute(new String[]{"la='ls -a'"}, context, null, null);
        assertEquals("ls -a", context.getAliases().get("la"));
    }

    @Test
    public void testListAliases() {
        command.execute(new String[]{"a=b"}, context, null, null);
        command.execute(new String[]{"c=d"}, context, null, null);

        CommandResult result = command.execute(new String[]{}, context, null, null);
        assertTrue(result.getMessage().contains("alias a='b'"));
        assertTrue(result.getMessage().contains("alias c='d'"));
    }
}
