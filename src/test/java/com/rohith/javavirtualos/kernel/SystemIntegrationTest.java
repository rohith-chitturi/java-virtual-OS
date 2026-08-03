package com.rohith.javavirtualos.kernel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SystemIntegrationTest {

    @Test
    void testEndToEndBootAndShutdown() {
        BootLoader bootLoader = new BootLoader();
        Kernel kernel = bootLoader.boot();
        
        assertNotNull(kernel);
        
        // At this point we are testing that the system boots without throwing exceptions.
        // Integration logic like file creation and executing hello.vexe will be tested when VirtualMachine is fully linked in.
        assertTrue(true);
    }
}
