package com.rohith.javavirtualos.services;

import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.kernel.process.ProcessManager;
import com.rohith.javavirtualos.kernel.process.VirtualProcess;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.List;

public class DefaultProcessService implements ProcessService {

    private final ProcessManager manager;

    public DefaultProcessService(ProcessManager manager) {
        this.manager = manager;
    }

    @Override
    public CommandResult listProcesses(ShellContext context) {
        List<VirtualProcess> processes = manager.listProcesses();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-10s %-12s %s\n", "PID", "USER", "STATE", "COMMAND"));
        for (VirtualProcess p : processes) {
            sb.append(String.format("%-6d %-10s %-12s %s\n", 
                p.getPid(), 
                p.getOwner().getUsername(), 
                p.getState(), 
                p.getName()));
        }
        return CommandResult.success(sb.toString().trim());
    }

    @Override
    public CommandResult killProcess(int pid, ShellContext context) {
        try {
            boolean success = manager.killProcess(pid, context.getCurrentUser());
            if (success) {
                return CommandResult.success("Process " + pid + " terminated.");
            } else {
                return CommandResult.failure("kill: process " + pid + " not found.");
            }
        } catch (SecurityException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public void executeAsProcess(String name, Runnable task, ShellContext context) {
        VirtualProcess process = manager.spawnProcess(name, context.getCurrentUser(), task);
        context.getOut().println("[" + process.getPid() + "] " + name + " started in background");
    }
}
