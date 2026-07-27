package com.rohith.javavirtualos.kernel.events;

import com.rohith.javavirtualos.kernel.network.IPAddress;
import com.rohith.javavirtualos.kernel.network.NetworkInterface;
import com.rohith.javavirtualos.kernel.network.Packet;
import com.rohith.javavirtualos.kernel.network.socket.VirtualSocket;

public abstract class NetworkEvent extends KernelEvent {
    
    public NetworkEvent() {
        super();
    }

    public static class SocketOpenedEvent extends NetworkEvent {
        private final VirtualSocket socket;
        public SocketOpenedEvent(VirtualSocket socket) {
            this.socket = socket;
        }
        public VirtualSocket getSocket() { return socket; }
        @Override
        public String getMessage() { return "Socket opened: " + socket.getProtocol(); }
    }

    public static class SocketClosedEvent extends NetworkEvent {
        private final VirtualSocket socket;
        public SocketClosedEvent(VirtualSocket socket) {
            this.socket = socket;
        }
        public VirtualSocket getSocket() { return socket; }
        @Override
        public String getMessage() { return "Socket closed: " + socket.getProtocol(); }
    }

    public static class PacketSentEvent extends NetworkEvent {
        private final Packet packet;
        public PacketSentEvent(Packet packet) {
            this.packet = packet;
        }
        public Packet getPacket() { return packet; }
        @Override
        public String getMessage() { return "Packet sent to " + packet.getDestinationIp(); }
    }

    public static class PacketReceivedEvent extends NetworkEvent {
        private final Packet packet;
        public PacketReceivedEvent(Packet packet) {
            this.packet = packet;
        }
        public Packet getPacket() { return packet; }
        @Override
        public String getMessage() { return "Packet received from " + packet.getSourceIp(); }
    }

    public static class PortBoundEvent extends NetworkEvent {
        private final IPAddress ip;
        private final int port;
        public PortBoundEvent(IPAddress ip, int port) {
            this.ip = ip;
            this.port = port;
        }
        public IPAddress getIp() { return ip; }
        public int getPort() { return port; }
        @Override
        public String getMessage() { return "Port bound: " + ip + ":" + port; }
    }

    public static class PortReleasedEvent extends NetworkEvent {
        private final IPAddress ip;
        private final int port;
        public PortReleasedEvent(IPAddress ip, int port) {
            this.ip = ip;
            this.port = port;
        }
        public IPAddress getIp() { return ip; }
        public int getPort() { return port; }
        @Override
        public String getMessage() { return "Port released: " + ip + ":" + port; }
    }

    public static class InterfaceUpEvent extends NetworkEvent {
        private final NetworkInterface netIf;
        public InterfaceUpEvent(NetworkInterface netIf) {
            this.netIf = netIf;
        }
        public NetworkInterface getNetworkInterface() { return netIf; }
        @Override
        public String getMessage() { return "Interface up: " + netIf.getName(); }
    }

    public static class InterfaceDownEvent extends NetworkEvent {
        private final NetworkInterface netIf;
        public InterfaceDownEvent(NetworkInterface netIf) {
            this.netIf = netIf;
        }
        public NetworkInterface getNetworkInterface() { return netIf; }
        @Override
        public String getMessage() { return "Interface down: " + netIf.getName(); }
    }
}
