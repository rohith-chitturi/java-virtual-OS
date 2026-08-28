package com.rohith.javavirtualos.kernel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KernelTest {

    @Test
    void testKernelInitialization() {
        BootLoader bootLoader = new BootLoader();
        Kernel kernel = bootLoader.boot();
        assertNotNull(kernel.getSystemContext());
        assertEquals("Java Virtual OS", kernel.getSystemContext().getOsName());
    }
}
