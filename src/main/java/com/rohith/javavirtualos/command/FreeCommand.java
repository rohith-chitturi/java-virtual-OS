package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class FreeCommand implements Command {

    private final FileSystemService fsService;

    public FreeCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        CommandResult meminfoRes = fsService.catFile("/proc/meminfo", context);
        if (!meminfoRes.isSuccess()) {
            return CommandResult.failure("Failed to read /proc/meminfo: " + meminfoRes.getMessage());
        }

        long total = 0;
        long free = 0;
        
        for (String line : meminfoRes.getMessage().split("\n")) {
            if (line.startsWith("MemTotal:")) {
                total = Long.parseLong(line.replaceAll("[^0-9]", ""));
            } else if (line.startsWith("MemFree:")) {
                free = Long.parseLong(line.replaceAll("[^0-9]", ""));
            }
        }

        long used = total - free;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("              total        used        free%n"));
        sb.append(String.format("Mem:     %10d  %10d  %10d%n", total, used, free));
        
        return CommandResult.success(sb.toString().trim());
    }

    @Override
    public String getName() {
        return "free";
    }

    @Override
    public String getDescription() {
        return "Display amount of free and used memory in the system";
    }
}
