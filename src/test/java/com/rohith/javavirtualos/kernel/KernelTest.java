package com.rohith.javavirtualos.kernel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KernelTest {

    @Test
    void testKernelInitialization() {
        Kernel kernel = new Kernel();
        kernel.initialize();
        assertNotNull(kernel.getSystemContext());
        assertEquals("Java Virtual OS", kernel.getSystemContext().getOsName());
    }
}
