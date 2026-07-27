package com.rohith.javavirtualos.kernel.device;

import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.device.drivers.NullDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceManagerTest {
    private DeviceManager deviceManager;
    private FileSystemManager fsManager;

    @BeforeEach
    void setUp() {
        KernelEventBus eventBus = new KernelEventBus();
        fsManager = new FileSystemManager();
        deviceManager = new DeviceManager(eventBus, fsManager);
    }

    @Test
    void testRegisterUnregister() {
        NullDevice nullDev = new NullDevice();
        deviceManager.registerDevice(nullDev);

        assertNotNull(deviceManager.getRegistry().getDriver("null"));
        assertEquals(DeviceState.ONLINE, nullDev.getDescriptor().getState());
        assertEquals(1, deviceManager.getStatistics().getInitCount());

        deviceManager.unregisterDevice("null");
        assertNull(deviceManager.getRegistry().getDriver("null"));
        assertEquals(DeviceState.OFFLINE, nullDev.getDescriptor().getState());
    }

    @Test
    void testDuplicateRegistrationFails() {
        NullDevice dev1 = new NullDevice();
        deviceManager.registerDevice(dev1);
        
        NullDevice dev2 = new NullDevice();
        deviceManager.registerDevice(dev2);
        
        // dev2 should fail validation and be marked FAILED
        assertEquals(DeviceState.ONLINE, dev1.getDescriptor().getState());
        assertEquals(DeviceState.FAILED, dev2.getDescriptor().getState());
    }
}
