package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.ConfigManager;
import com.rohith.javavirtualos.kernel.SystemContext;
import com.rohith.javavirtualos.shell.ShellContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.rohith.javavirtualos.kernel.User;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CommandTests {

    private ShellContext shellContext;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        ConfigManager cm = new ConfigManager();
        SystemContext systemContext = new SystemContext(cm);
        outContent = new ByteArrayOutputStream();
        PrintStream outStream = new PrintStream(outContent);
        User mockRoot = new User("root", "root");
        shellContext = new ShellContext(systemContext, mockRoot, outStream, System.in);
    }

    @Test
    void testEchoCommand() {
        Command echo = new EchoCommand();
        CommandResult result = echo.execute(new String[]{"Hello", "World"}, shellContext);
        assertTrue(result.isSuccess());
        assertEquals("Hello World", result.getMessage());
    }

    @Test
    void testPwdCommand() {
        Command pwd = new PwdCommand();
        CommandResult result = pwd.execute(new String[]{}, shellContext);
        assertTrue(result.isSuccess());
        assertEquals("/", result.getMessage());
    }
    
    @Test
    void testDateCommand() {
        Command date = new DateCommand();
        CommandResult result = date.execute(new String[]{}, shellContext);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains(LocalDate.now().toString()));
    }
    
    @Test
    void testExitCommand() {
        Command exit = new ExitCommand();
        CommandResult result = exit.execute(new String[]{}, shellContext);
        assertTrue(result.shouldTerminateShell(), "Exit command should return terminate flag");
        assertEquals("Goodbye!", result.getMessage());
    }
}
