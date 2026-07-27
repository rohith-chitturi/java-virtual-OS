package com.rohith.javavirtualos.command.network;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.kernel.network.NetworkInterface;
import com.rohith.javavirtualos.kernel.network.NetworkManager;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.Map;

public class IfConfigCommand implements Command {
    private final NetworkManager networkManager;

    public IfConfigCommand(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public String getName() { return "ifconfig"; }

    @Override
    public String getDescription() { return "View active network interfaces"; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        Map<String, NetworkInterface> routes = networkManager.getRoutingTable().getRoutes();
        // Just deduping by interface
        routes.values().stream().distinct().forEach(netIf -> {
            context.getOut().println(netIf.getName() + ": flags=" + (netIf.isUp() ? "UP,RUNNING" : "DOWN"));
            context.getOut().println("        inet " + netIf.getIpAddress().getAddressString() + "  netmask 255.0.0.0");
            context.getOut().println("        ether " + netIf.getMacAddress());
            context.getOut().println();
        });
        return CommandResult.success();
    }
}
