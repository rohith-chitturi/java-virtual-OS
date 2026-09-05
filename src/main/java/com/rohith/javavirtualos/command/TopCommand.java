package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class TopCommand implements Command {

    private final FileSystemService fsService;

    public TopCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        StringBuilder sb = new StringBuilder();

        // 1. Read Uptime
        CommandResult uptimeRes = fsService.catFile("/proc/uptime", context);
        if (uptimeRes.isSuccess()) {
            sb.append("top - uptime: ").append(uptimeRes.getMessage().split(" ")[0]).append(" sec\n");
        }

        // 2. Read Meminfo
        CommandResult meminfoRes = fsService.catFile("/proc/meminfo", context);
        if (meminfoRes.isSuccess()) {
            sb.append("Memory: ").append(meminfoRes.getMessage().replace("\n", ", ")).append("\n");
        }
        sb.append("\n");

        // 3. Processes
        sb.append(String.format("%-5s %-8s %-5s%n", "PID", "STATE", "COMMAND"));
        CommandResult lsResult = fsService.listDirectory("/proc", context);
        if (lsResult.isSuccess()) {
            String[] lines = lsResult.getMessage().split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("[DIR]")) {
                    String pidStr = line.substring(6).trim();
                    if (pidStr.matches("\\d+")) {
                        CommandResult statRes = fsService.catFile("/proc/" + pidStr + "/stat", context);
                        if (statRes.isSuccess()) {
                            String stat = statRes.getMessage();
                            String[] parts = stat.split(" ");
                            if (parts.length >= 3) {
                                String pid = parts[0];
                                String name = parts[1];
                                if (name.startsWith("(") && name.endsWith(")")) {
                                    name = name.substring(1, name.length() - 1);
                                }
                                String state = parts[2];
                                sb.append(String.format("%-5s %-8s %-5s%n", pid, state, name));
                            }
                        }
                    }
                }
            }
        }
        
        return CommandResult.success(sb.toString().trim());
    }

    @Override
    public String getName() {
        return "top";
    }

    @Override
    public String getDescription() {
        return "Display JavaOS tasks (snapshot)";
    }
}
