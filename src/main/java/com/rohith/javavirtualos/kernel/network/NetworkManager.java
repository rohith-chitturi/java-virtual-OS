package com.rohith.javavirtualos.kernel.network;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.NetworkEvent.PacketReceivedEvent;
import com.rohith.javavirtualos.kernel.events.NetworkEvent.PacketSentEvent;
import com.rohith.javavirtualos.kernel.network.socket.PortManager;
import com.rohith.javavirtualos.kernel.network.socket.VirtualSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NetworkManager {
    private final KernelEventBus eventBus;
    private final NetworkStatistics statistics;
    private final RoutingTable routingTable;
    private final PortManager portManager;
    private final List<VirtualSocket> activeSockets;

    public NetworkManager(KernelEventBus eventBus) {
        this.eventBus = eventBus;
        this.statistics = new NetworkStatistics();
        this.routingTable = new RoutingTable();
        this.portManager = new PortManager();
        this.activeSockets = new ArrayList<>();

        initializeLoopback();
    }

    private void initializeLoopback() {
        NetworkInterface loopback = new NetworkInterface("lo0", IPAddress.LOOPBACK, "00:00:00:00:00:00");
        loopback.setUp(true);
        routingTable.addRoute("127.0.0.0/8", loopback);
    }

    public void registerSocket(VirtualSocket socket) {
        activeSockets.add(socket);
        statistics.recordSocketOpened();
    }

    public void unregisterSocket(VirtualSocket socket) {
        activeSockets.remove(socket);
        statistics.recordSocketClosed();
    }

    public void sendPacket(Packet packet) {
        statistics.recordPacketSent(packet.getPayload().length);
        eventBus.publish(new PacketSentEvent(packet));

        NetworkInterface route = routingTable.route(packet.getDestinationIp());
        if (route == null || !route.isUp()) {
            // Drop packet, no route to host
            return;
        }

        // Simulate local routing: if destination is a local interface, handle it internally
        if (isLocalIp(packet.getDestinationIp())) {
            handleIncomingPacket(packet);
        }
    }

    private boolean isLocalIp(IPAddress ip) {
        for (NetworkInterface netIf : routingTable.getRoutes().values()) {
            if (netIf.getIpAddress().equals(ip)) return true;
        }
        return false;
    }

    public void handleIncomingPacket(Packet packet) {
        statistics.recordPacketReceived(packet.getPayload().length);
        eventBus.publish(new PacketReceivedEvent(packet));

        if (packet.getProtocol() == Protocol.ICMP) {
            // Simplified ICMP Echo Reply (Ping)
            // Just echo it back, swapping src and dst
            Packet reply = new Packet(
                packet.getDestinationIp(),
                packet.getDestinationPort(),
                packet.getSourceIp(),
                packet.getSourcePort(),
                Protocol.ICMP,
                packet.getPayload()
            );
            // Recursion check: don't loop indefinitely if it's addressed to itself
            if (!packet.getSourceIp().equals(packet.getDestinationIp())) {
                sendPacket(reply);
            } else {
                // It was a local ping. 
                // We don't want an infinite ping-pong. In a real OS, ICMP replies go to the socket that initiated the ping.
                // We'll route it to the socket.
                routeToSocket(packet);
            }
            return;
        }

        routeToSocket(packet);
    }
    
    private void routeToSocket(Packet packet) {
        Optional<VirtualSocket> targetSocket = activeSockets.stream()
            .filter(s -> s.getProtocol() == packet.getProtocol() &&
                         s.getLocalPort() == packet.getDestinationPort() &&
                         (s.getLocalIp().equals(IPAddress.ANY) || s.getLocalIp().equals(packet.getDestinationIp())))
            .findFirst();

        targetSocket.ifPresent(socket -> socket.handlePacket(packet));
    }

    public NetworkStatistics getStatistics() { return statistics; }
    public RoutingTable getRoutingTable() { return routingTable; }
    public PortManager getPortManager() { return portManager; }
    public List<VirtualSocket> getActiveSockets() { return new ArrayList<>(activeSockets); }
}
