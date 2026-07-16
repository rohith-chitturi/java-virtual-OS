package com.rohith.javavirtualos.shell;

import com.rohith.javavirtualos.kernel.ConfigManager;
import com.rohith.javavirtualos.kernel.SystemContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ShellTest {

    private SystemContext systemContext;

    @BeforeEach
    void setUp() {
        ConfigManager cm = new ConfigManager();
        systemContext = new SystemContext(cm);
    }

    @Test
    void testShellInitialization() {
        Shell shell = new Shell(systemContext);
        assertNotNull(shell);
    }
}
