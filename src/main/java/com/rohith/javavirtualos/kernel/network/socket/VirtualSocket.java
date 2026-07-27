package com.rohith.javavirtualos.kernel.network.socket;

import com.rohith.javavirtualos.kernel.network.IPAddress;
import com.rohith.javavirtualos.kernel.network.Packet;
import com.rohith.javavirtualos.kernel.network.Protocol;

public abstract class VirtualSocket {
    protected final Protocol protocol;
    protected IPAddress localIp;
    protected int localPort;
    protected IPAddress remoteIp;
    protected int remotePort;
    protected ConnectionState state;

    protected VirtualSocket(Protocol protocol) {
        this.protocol = protocol;
        this.state = ConnectionState.CLOSED;
    }

    public Protocol getProtocol() { return protocol; }
    public IPAddress getLocalIp() { return localIp; }
    public int getLocalPort() { return localPort; }
    public IPAddress getRemoteIp() { return remoteIp; }
    public int getRemotePort() { return remotePort; }
    public ConnectionState getState() { return state; }
    
    public void setLocalAddress(IPAddress ip, int port) {
        this.localIp = ip;
        this.localPort = port;
    }
    
    public void setRemoteAddress(IPAddress ip, int port) {
        this.remoteIp = ip;
        this.remotePort = port;
    }

    public abstract void handlePacket(Packet packet);
    
    public void close() {
        this.state = ConnectionState.CLOSED;
    }
}
