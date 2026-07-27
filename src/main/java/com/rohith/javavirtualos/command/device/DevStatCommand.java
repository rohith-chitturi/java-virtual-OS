package com.rohith.javavirtualos.command.device;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.kernel.device.DeviceManager;

public class DevStatCommand implements Command {
    private final DeviceManager deviceManager;

    public DevStatCommand(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    @Override
    public String getName() {
        return "devstat";
    }

    @Override
    public String getDescription() {
        return "Displays global device statistics";
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        context.getOut().println("Device Subsystem Statistics:");
        context.getOut().println("----------------------------");
        context.getOut().println("Init Count    : " + deviceManager.getStatistics().getInitCount());
        context.getOut().println("Total Reads   : " + deviceManager.getStatistics().getReads());
        context.getOut().println("Total Writes  : " + deviceManager.getStatistics().getWrites());
        context.getOut().println("Bytes Read    : " + deviceManager.getStatistics().getBytesRead());
        context.getOut().println("Bytes Written : " + deviceManager.getStatistics().getBytesWritten());
        context.getOut().println("Errors        : " + deviceManager.getStatistics().getErrors());
        
        return CommandResult.success();
    }
}
