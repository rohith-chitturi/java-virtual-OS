package com.rohith.javavirtualos.command.network;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.kernel.network.IPAddress;
import com.rohith.javavirtualos.kernel.network.NetworkManager;
import com.rohith.javavirtualos.kernel.network.Packet;
import com.rohith.javavirtualos.kernel.network.Protocol;
import com.rohith.javavirtualos.kernel.network.socket.UDPSocket;
import com.rohith.javavirtualos.shell.ShellContext;

public class PingCommand implements Command {
    private final NetworkManager networkManager;

    public PingCommand(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public String getName() { return "ping"; }

    @Override
    public String getDescription() { return "Send ICMP ECHO_REQUEST to network hosts"; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length < 1) {
            context.getOut().println("Usage: ping <destination>");
            return CommandResult.failure("Missing destination");
        }

        String destStr = args[0];
        if (!IPAddress.isValid(destStr)) {
            context.getOut().println("ping: unknown host " + destStr);
            return CommandResult.failure("Unknown host");
        }

        IPAddress destIp = new IPAddress(destStr);
        int srcPort = networkManager.getPortManager().bindEphemeral(IPAddress.ANY);
        if (srcPort == -1) {
            context.getOut().println("ping: no ports available");
            return CommandResult.failure("No ports available");
        }

        UDPSocket pingSocket = new UDPSocket() {
            private int repliesReceived = 0;
            @Override
            public void handlePacket(Packet packet) {
                if (packet.getProtocol() == Protocol.ICMP) {
                    repliesReceived++;
                    context.getOut().println(packet.getPayload().length + " bytes from " + packet.getSourceIp() + ": icmp_seq=" + repliesReceived + " ttl=" + packet.getTtl() + " time=1ms");
                }
            }
        };
        pingSocket.setLocalAddress(IPAddress.ANY, srcPort);
        networkManager.registerSocket(pingSocket);

        context.getOut().println("PING " + destStr + " 56 bytes of data.");

        try {
            for (int i = 0; i < 4; i++) {
                Packet p = new Packet(IPAddress.LOOPBACK, srcPort, destIp, 0, Protocol.ICMP, new byte[56]);
                networkManager.sendPacket(p);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            networkManager.unregisterSocket(pingSocket);
            networkManager.getPortManager().unbind(IPAddress.ANY, srcPort);
        }

        return CommandResult.success();
    }
}
