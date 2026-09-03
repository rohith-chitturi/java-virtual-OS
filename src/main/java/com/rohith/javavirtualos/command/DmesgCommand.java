package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class DmesgCommand implements Command {

    private final FileSystemService fsService;

    public DmesgCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        CommandResult kmsgRes = fsService.catFile("/proc/kmsg", context);
        if (!kmsgRes.isSuccess()) {
            return CommandResult.failure("Failed to read kernel logs: " + kmsgRes.getMessage());
        }

        return CommandResult.success(kmsgRes.getMessage().trim());
    }

    @Override
    public String getName() {
        return "dmesg";
    }

    @Override
    public String getDescription() {
        return "Print or control the kernel ring buffer";
    }
}
