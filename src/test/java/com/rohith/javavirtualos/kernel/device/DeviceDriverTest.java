package com.rohith.javavirtualos.kernel.device;

import com.rohith.javavirtualos.kernel.device.drivers.NullDevice;
import com.rohith.javavirtualos.kernel.device.drivers.ZeroDevice;
import com.rohith.javavirtualos.kernel.device.drivers.RandomDevice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceDriverTest {
    @Test
    void testNullDevice() throws Exception {
        NullDevice dev = new NullDevice();
        dev.init();
        assertEquals(DeviceState.ONLINE, dev.getDescriptor().getState());
        assertEquals(0, dev.read(100).length); // EOF immediately
        assertEquals(10, dev.write(new byte[10])); // Discards but says written
    }

    @Test
    void testZeroDevice() throws Exception {
        ZeroDevice dev = new ZeroDevice();
        dev.init();
        byte[] data = dev.read(5);
        assertEquals(5, data.length);
        for (byte b : data) {
            assertEquals((byte) 0, b);
        }
    }

    @Test
    void testRandomDevice() throws Exception {
        RandomDevice dev = new RandomDevice();
        dev.init();
        byte[] data1 = dev.read(10);
        byte[] data2 = dev.read(10);
        assertEquals(10, data1.length);
        assertNotEquals(java.util.Arrays.toString(data1), java.util.Arrays.toString(data2));
    }
}
