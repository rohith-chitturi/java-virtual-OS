package com.rohith.javavirtualos.command.device;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.kernel.device.DeviceManager;

public class DevicesCommand implements Command {
    private final DeviceManager deviceManager;

    public DevicesCommand(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    @Override
    public String getName() {
        return "devices";
    }

    @Override
    public String getDescription() {
        return "Alias for lsdev to view device registry";
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return new LsDevCommand(deviceManager).execute(args, context);
    }
}
