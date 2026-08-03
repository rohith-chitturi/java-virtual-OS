package com.rohith.javavirtualos.kernel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BootSequenceTest {

    @Test
    void testBootLoaderInitializesKernelCorrectly() {
        BootLoader bootLoader = new BootLoader();
        Kernel kernel = bootLoader.boot();
        
        assertNotNull(kernel);
        assertNotNull(kernel.getSystemContext());
        assertEquals("Java Virtual OS", kernel.getSystemContext().getOsName());
        
        // Assert that the kernel isn't null and it booted properly
        assertNotNull(kernel);
    }
}
