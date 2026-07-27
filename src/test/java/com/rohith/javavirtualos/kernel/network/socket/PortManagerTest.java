package com.rohith.javavirtualos.kernel.network.socket;

import com.rohith.javavirtualos.kernel.network.IPAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortManagerTest {
    private PortManager portManager;

    @BeforeEach
    void setUp() {
        portManager = new PortManager();
    }

    @Test
    void testBindAndUnbind() {
        assertTrue(portManager.bind(IPAddress.LOOPBACK, 80));
        assertFalse(portManager.bind(IPAddress.LOOPBACK, 80)); // Collision

        assertTrue(portManager.bind(IPAddress.ANY, 80)); // Different IP binding allowed (simplified)

        portManager.unbind(IPAddress.LOOPBACK, 80);
        assertTrue(portManager.bind(IPAddress.LOOPBACK, 80)); // Should work now
    }

    @Test
    void testEphemeralPorts() {
        int port1 = portManager.bindEphemeral(IPAddress.LOOPBACK);
        assertTrue(port1 >= 49152 && port1 <= 65535);

        int port2 = portManager.bindEphemeral(IPAddress.LOOPBACK);
        assertNotEquals(port1, port2);
    }
}
