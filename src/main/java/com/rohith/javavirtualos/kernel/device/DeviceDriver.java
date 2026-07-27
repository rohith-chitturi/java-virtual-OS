package com.rohith.javavirtualos.kernel.device;

import java.io.IOException;

public interface DeviceDriver {
    DeviceDescriptor getDescriptor();
    void init() throws IOException;
    void shutdown();
    byte[] read(int maxBytes) throws IOException;
    int write(byte[] data) throws IOException;
    boolean healthCheck();
}
