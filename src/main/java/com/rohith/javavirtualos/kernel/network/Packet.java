package com.rohith.javavirtualos.kernel.network;

import java.util.UUID;

public class Packet {
    private final String id;
    private final IPAddress sourceIp;
    private final IPAddress destinationIp;
    private final int sourcePort;
    private final int destinationPort;
    private final Protocol protocol;
    private final byte[] payload;
    private int ttl;

    public Packet(IPAddress sourceIp, int sourcePort, IPAddress destinationIp, int destinationPort, Protocol protocol, byte[] payload) {
        this.id = UUID.randomUUID().toString();
        this.sourceIp = sourceIp;
        this.sourcePort = sourcePort;
        this.destinationIp = destinationIp;
        this.destinationPort = destinationPort;
        this.protocol = protocol;
        this.payload = payload != null ? payload : new byte[0];
        this.ttl = 64; // Default TTL
    }

    public String getId() { return id; }
    public IPAddress getSourceIp() { return sourceIp; }
    public IPAddress getDestinationIp() { return destinationIp; }
    public int getSourcePort() { return sourcePort; }
    public int getDestinationPort() { return destinationPort; }
    public Protocol getProtocol() { return protocol; }
    public byte[] getPayload() { return payload; }
    public int getTtl() { return ttl; }
    
    public void decrementTtl() {
        this.ttl--;
    }

    @Override
    public String toString() {
        return String.format("Packet[%s] %s:%d -> %s:%d [%s] (TTL=%d, %d bytes)",
            id.substring(0, 8), sourceIp, sourcePort, destinationIp, destinationPort, protocol, ttl, payload.length);
    }
}
