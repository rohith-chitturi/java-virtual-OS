package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.ConfigManager;
import com.rohith.javavirtualos.kernel.SystemContext;
import com.rohith.javavirtualos.shell.ShellContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        shellContext = new ShellContext(systemContext, outStream, System.in);
    }

    @Test
    void testEchoCommand() {
        Command echo = new EchoCommand();
        boolean result = echo.execute(new String[]{"Hello", "World"}, shellContext);
        assertTrue(result);
        assertEquals("Hello World" + System.lineSeparator(), outContent.toString());
    }

    @Test
    void testPwdCommand() {
        Command pwd = new PwdCommand();
        boolean result = pwd.execute(new String[]{}, shellContext);
        assertTrue(result);
        assertEquals("/" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    void testDateCommand() {
        Command date = new DateCommand();
        boolean result = date.execute(new String[]{}, shellContext);
        assertTrue(result);
        assertTrue(outContent.toString().contains(LocalDate.now().toString()));
    }
    
    @Test
    void testExitCommand() {
        Command exit = new ExitCommand();
        boolean result = exit.execute(new String[]{}, shellContext);
        assertFalse(result, "Exit command should return false to break the shell loop");
        assertEquals("Goodbye!" + System.lineSeparator(), outContent.toString());
    }
}
