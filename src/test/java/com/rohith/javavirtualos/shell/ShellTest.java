package com.rohith.javavirtualos.shell;

import com.rohith.javavirtualos.kernel.ConfigManager;
import com.rohith.javavirtualos.kernel.SystemContext;
import com.rohith.javavirtualos.kernel.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        UserManager userManager = new UserManager();
        Shell shell = new Shell(systemContext, null, null, userManager, null);
        assertNotNull(shell);
    }
}
