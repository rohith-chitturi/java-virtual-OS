package com.rohith.javavirtualos.services;

import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.kernel.process.manager.ProcessManager;
import com.rohith.javavirtualos.kernel.process.manager.ProcessTask;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.List;

public class DefaultProcessService implements ProcessService {

    private final ProcessManager manager;

    public DefaultProcessService(ProcessManager manager) {
        this.manager = manager;
    }

    @Override
    public CommandResult listProcesses(ShellContext context) {
        List<ProcessControlBlock> processes = manager.listProcesses();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-12s %-12s %-5s %s\n", "PID", "NAME", "STATE", "PRI", "MEM(KB)"));
        for (ProcessControlBlock p : processes) {
            sb.append(String.format("%-6d %-12s %-12s %-5d %d\n", 
                p.getPid(), 
                p.getCommandName(), 
                p.getState(), 
                p.getSchedulingInfo().getPriority(),
                p.getResourceInfo().getMemoryUsage() / 1024));
        }
        return CommandResult.success(sb.toString().trim());
    }

    @Override
    public CommandResult killProcess(int pid, ShellContext context) {
        try {
            manager.terminateProcess(pid, context.getCurrentUser());
            return CommandResult.success("Process " + pid + " terminated.");
        } catch (Exception e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public void executeAsProcess(String name, Runnable task, ShellContext context) {
        ProcessTask pTask = new ProcessTask(task);
        ProcessControlBlock pcb = manager.createProcess(name, context.getCurrentUser(), pTask, 1);
        manager.startProcess(pcb.getPid());
        context.getOut().println("[" + pcb.getPid() + "] " + name + " started in background");
    }
    
    public ProcessManager getManager() {
        return manager;
    }
}
