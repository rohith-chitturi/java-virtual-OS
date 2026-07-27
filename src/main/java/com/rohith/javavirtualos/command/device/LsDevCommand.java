package com.rohith.javavirtualos.command.device;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.kernel.device.DeviceManager;
import com.rohith.javavirtualos.kernel.device.DeviceDriver;

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
        context.getOut().printf("%-15s %-10s %-15s %-10s%n", "NAME", "TYPE", "MOUNT", "STATE");
        context.getOut().println("-".repeat(50));
        
        for (DeviceDriver driver : deviceManager.getRegistry().getAllDrivers()) {
            context.getOut().printf("%-15s %-10s %-15s %-10s%n", 
                driver.getDescriptor().getName(), 
                driver.getDescriptor().getType(),
                driver.getDescriptor().getMountPath(),
                driver.getDescriptor().getState());
        }
        return CommandResult.success();
    }
}
