package com.rohith.javavirtualos.kernel.network.socket;

import com.rohith.javavirtualos.kernel.network.Packet;
import com.rohith.javavirtualos.kernel.network.Protocol;

public class UDPSocket extends VirtualSocket {
    public UDPSocket() {
        super(Protocol.UDP);
    }

    @Override
    public void handlePacket(Packet packet) {
        // Implementation for later
    }
}
