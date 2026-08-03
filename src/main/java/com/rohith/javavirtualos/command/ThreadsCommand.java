package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.services.DefaultProcessService;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.List;

public class ThreadsCommand implements Command {

    private final ProcessService processService;

    public ThreadsCommand(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length == 0) {
            return CommandResult.failure("threads: missing pid");
        }
        if (!(processService instanceof DefaultProcessService)) {
            return CommandResult.failure("Process service does not support threads command.");
        }
        
        try {
            int pid = Integer.parseInt(args[0]);
            ProcessControlBlock pcb = ((DefaultProcessService) processService).getManager().findByPID(pid);
            int tgid = pcb.getTgid();
            
            List<ProcessControlBlock> allProcs = ((DefaultProcessService) processService).getManager().listProcesses();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Threads for TGID %d (%s):\n", tgid, pcb.getCommandName()));
            sb.append(String.format("%-6s %-12s %-12s %-5s %-6s\n", "PID", "NAME", "STATE", "PRI", "CORE"));
            
            for (ProcessControlBlock p : allProcs) {
                if (p.getTgid() == tgid) {
                    String core = p.getActiveCore() >= 0 ? String.valueOf(p.getActiveCore()) : "-";
                    sb.append(String.format("%-6d %-12s %-12s %-5d %-6s\n", 
                        p.getPid(),
                        p.getCommandName(), 
                        p.getState(), 
                        p.getSchedulingInfo().getPriority(),
                        core));
                }
            }
            return CommandResult.success(sb.toString().trim());
        } catch (NumberFormatException e) {
            return CommandResult.failure("threads: invalid pid format");
        } catch (Exception e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "threads";
    }

    @Override
    public String getDescription() {
        return "List all Lightweight Processes (threads) for a given PID";
    }
}
