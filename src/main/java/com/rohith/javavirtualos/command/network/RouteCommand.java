package com.rohith.javavirtualos.command.network;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.kernel.network.NetworkManager;
import com.rohith.javavirtualos.shell.ShellContext;

public class RouteCommand implements Command {
    private final NetworkManager networkManager;

    public RouteCommand(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public String getName() { return "route"; }

    @Override
    public String getDescription() { return "Show the routing table"; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        context.getOut().println("Kernel IP routing table");
        context.getOut().println("Destination     Iface");
        networkManager.getRoutingTable().getRoutes().forEach((dest, netIf) -> {
            context.getOut().printf("%-15s %s\n", dest, netIf.getName());
        });
        return CommandResult.success();
    }
}
