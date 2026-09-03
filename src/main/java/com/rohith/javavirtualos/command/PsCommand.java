package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

public class PsCommand implements Command {

    private final com.rohith.javavirtualos.services.FileSystemService fsService;

    public PsCommand(com.rohith.javavirtualos.services.FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        CommandResult lsResult = fsService.listDirectory("/proc", context);
        if (!lsResult.isSuccess()) {
            return CommandResult.failure("Failed to read /proc: " + lsResult.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-10s %-8s %-5s%n", "PID", "USER", "STATE", "CMD"));

        String[] lines = lsResult.getMessage().split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("[DIR]")) {
                String pidStr = line.substring(6).trim();
                // Check if it's a numeric PID directory
                if (pidStr.matches("\\d+")) {
                    CommandResult statusResult = fsService.catFile("/proc/" + pidStr + "/stat", context);
                    if (statusResult.isSuccess()) {
                        // format: PID (NAME) STATE PPID PGID
                        // Example: 42 (loop) R 1 42
                        String stat = statusResult.getMessage();
                        String[] parts = stat.split(" ");
                        if (parts.length >= 3) {
                            String pid = parts[0];
                            String name = parts[1];
                            if (name.startsWith("(") && name.endsWith(")")) {
                                name = name.substring(1, name.length() - 1);
                            }
                            String state = parts[2];
                            // To get user, we can read status file or just say root for now
                            sb.append(String.format("%-5s %-10s %-8s %-5s%n", pid, "root", state, name));
                        }
                    }
                }
            }
        }
        return CommandResult.success(sb.toString().trim());
    }

    @Override
    public String getName() {
        return "ps";
    }

    @Override
    public String getDescription() {
        return "List active processes by reading /proc";
    }
}
