package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class VmstatCommand implements Command {

    private final FileSystemService fsService;

    public VmstatCommand(FileSystemService fsService) {
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

        // Just a mock snapshot for now representing memory stats.
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("procs -----------memory---------- ---swap-- -----io---- -system-- ------cpu-----%n"));
        sb.append(String.format(" r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa st%n"));
        sb.append(String.format(" 1  0      0 %6d      0      0    0    0     0     0    0    0  0  0 100  0  0%n", free));
        
        return CommandResult.success(sb.toString().trim());
    }

    @Override
    public String getName() {
        return "vmstat";
    }

    @Override
    public String getDescription() {
        return "Report virtual memory statistics (snapshot)";
    }
}
