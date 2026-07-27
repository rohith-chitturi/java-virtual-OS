package com.rohith.javavirtualos.kernel.network;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.network.socket.TCPSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetworkManagerTest {
    private NetworkManager networkManager;

    @BeforeEach
    void setUp() {
        KernelEventBus eventBus = new KernelEventBus();
        networkManager = new NetworkManager(eventBus);
    }

    @Test
    void testLoopbackInitialization() {
        assertNotNull(networkManager.getRoutingTable().route(IPAddress.LOOPBACK));
        assertTrue(networkManager.getRoutingTable().route(IPAddress.LOOPBACK).isUp());
    }

    @Test
    void testSocketRegistration() {
        TCPSocket socket = new TCPSocket();
        networkManager.registerSocket(socket);
        assertEquals(1, networkManager.getActiveSockets().size());
        assertEquals(1, networkManager.getStatistics().getActiveSockets());

        networkManager.unregisterSocket(socket);
        assertEquals(0, networkManager.getActiveSockets().size());
        assertEquals(0, networkManager.getStatistics().getActiveSockets());
    }
}
