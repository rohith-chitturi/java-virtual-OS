package com.rohith.javavirtualos.command.device;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.kernel.device.DeviceManager;
import com.rohith.javavirtualos.kernel.device.DeviceDriver;
import com.rohith.javavirtualos.kernel.device.DeviceCapability;

import java.util.stream.Collectors;

public class LsDevCommand implements Command {
    private final DeviceManager deviceManager;

    public LsDevCommand(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    @Override
    public String getName() {
        return "lsdev";
    }

    @Override
    public String getDescription() {
        return "Lists all registered hardware devices";
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        context.getOut().println("Registered Devices:");
        context.getOut().printf("%-10s %-12s %-15s %-10s %-25s%n", "NAME", "TYPE", "VENDOR", "STATE", "CAPABILITIES");
        context.getOut().println("-".repeat(75));
        
        for (DeviceDriver driver : deviceManager.getRegistry().getAllDrivers()) {
            String caps = driver.getDescriptor().getCapabilities().stream()
                .map(DeviceCapability::name)
                .collect(Collectors.joining(", "));

            context.getOut().printf("%-10s %-12s %-15s %-10s %-25s%n", 
                driver.getDescriptor().getName(), 
                driver.getDescriptor().getType(),
                driver.getDescriptor().getVendor(),
                driver.getDescriptor().getState(),
                caps);
        }
        return CommandResult.success();
    }
}
