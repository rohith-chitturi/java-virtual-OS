package com.rohith.javavirtualos.kernel.network.socket;

import com.rohith.javavirtualos.kernel.network.Packet;
import com.rohith.javavirtualos.kernel.network.Protocol;

public class TCPSocket extends VirtualSocket {
    public TCPSocket() {
        super(Protocol.TCP);
    }

    @Override
    public void handlePacket(Packet packet) {
        // Implementation for later
    }
}
