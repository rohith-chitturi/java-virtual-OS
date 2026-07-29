package com.rohith.javavirtualos.services;

import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.kernel.process.manager.ProcessManager;
import com.rohith.javavirtualos.kernel.process.manager.ProcessTask;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.List;

public class DefaultProcessService implements ProcessService {

    private final ProcessManager manager;
    private com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher dispatcher;

    public DefaultProcessService(ProcessManager manager) {
        this.manager = manager;
    }
    
    public void setDispatcher(com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public CommandResult listProcesses(ShellContext context) {
        List<ProcessControlBlock> processes = manager.listProcesses();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-6s %-6s %-12s %-12s %-5s %-6s %s\n", "PID", "TGID", "PGID", "NAME", "STATE", "PRI", "CORE", "MEM(KB)"));
        for (ProcessControlBlock p : processes) {
            String core = p.getActiveCore() >= 0 ? String.valueOf(p.getActiveCore()) : "-";
            sb.append(String.format("%-6d %-6d %-6d %-12s %-12s %-5d %-6s %d\n", 
                p.getPid(), 
                p.getTgid(),
                p.getPgid(),
                p.getCommandName(), 
                p.getState(), 
                p.getSchedulingInfo().getPriority(),
                core,
                p.getResourceInfo().getMemoryUsage() / 1024));
        }
        return CommandResult.success(sb.toString().trim());
    }

    @Override
    public CommandResult killProcess(int pid, ShellContext context) {
        try {
            if (pid < 0) {
                int pgid = -pid;
                List<ProcessControlBlock> group = manager.findByPgid(pgid);
                if (group.isEmpty()) {
                    return CommandResult.failure("kill: no process found in group " + pgid);
                }
                for (ProcessControlBlock p : group) {
                    manager.terminateProcess(p.getPid(), context.getCurrentUser());
                }
                return CommandResult.success("Process group " + pgid + " terminated.");
            } else {
                manager.terminateProcess(pid, context.getCurrentUser());
                return CommandResult.success("Process " + pid + " terminated.");
            }
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
