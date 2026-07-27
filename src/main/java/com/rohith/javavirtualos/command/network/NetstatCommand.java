package com.rohith.javavirtualos.command.network;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.kernel.network.NetworkManager;
import com.rohith.javavirtualos.kernel.network.socket.VirtualSocket;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.List;

public class NetstatCommand implements Command {
    private final NetworkManager networkManager;

    public NetstatCommand(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public String getName() { return "netstat"; }

    @Override
    public String getDescription() { return "Print network connections"; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        List<VirtualSocket> sockets = networkManager.getActiveSockets();
        context.getOut().println("Active Internet connections (w/o servers)");
        context.getOut().println("Proto Local Address          Foreign Address        State");
        
        for (VirtualSocket socket : sockets) {
            String localAddr = socket.getLocalIp() != null ? socket.getLocalIp().getAddressString() + ":" + socket.getLocalPort() : "*:*";
            String foreignAddr = socket.getRemoteIp() != null ? socket.getRemoteIp().getAddressString() + ":" + socket.getRemotePort() : "*:*";
            context.getOut().printf("%-5s %-22s %-22s %s\n", 
                socket.getProtocol().name(), localAddr, foreignAddr, socket.getState().name());
        }
        
        context.getOut().println("\nNetwork Statistics:");
        context.getOut().println("Packets sent: " + networkManager.getStatistics().getPacketsSent());
        context.getOut().println("Packets recv: " + networkManager.getStatistics().getPacketsReceived());
        
        return CommandResult.success();
    }
}
