package com.rohith.javavirtualos.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandResultTest {

    @Test
    public void testSuccess() {
        CommandResult result = CommandResult.success();
        assertTrue(result.isSuccess());
        assertNull(result.getMessage());
        assertFalse(result.shouldTerminateShell());
    }

    @Test
    public void testSuccessWithMessage() {
        CommandResult result = CommandResult.success("Done");
        assertTrue(result.isSuccess());
        assertEquals("Done", result.getMessage());
    }

    @Test
    public void testFailure() {
        CommandResult result = CommandResult.failure("Error");
        assertFalse(result.isSuccess());
        assertEquals("Error", result.getMessage());
    }

    @Test
    public void testTerminate() {
        CommandResult result = CommandResult.terminate("Goodbye");
        assertTrue(result.isSuccess());
        assertEquals("Goodbye", result.getMessage());
        assertTrue(result.shouldTerminateShell());
    }
}
