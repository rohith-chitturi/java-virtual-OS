package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.List;

public class RunQueueCommand implements Command {
    private final ProcessService processService;

    public RunQueueCommand(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public String getName() { return "runqueue"; }

    @Override
    public String getDescription() { return "Displays the run queues for each core"; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        KernelDispatcher dispatcher = processService.getDispatcher();
        if (dispatcher == null) return CommandResult.failure("Scheduler not initialized.");

        StringBuilder sb = new StringBuilder();
        List<Scheduler> schedulers = dispatcher.getCoreSchedulers();
        for (int i = 0; i < schedulers.size(); i++) {
            sb.append("Core ").append(i).append(" (").append(schedulers.get(i).getName()).append(") Queue:\n");
            List<ProcessControlBlock> q = schedulers.get(i).getReadyQueue().getQueue();
            if (q.isEmpty()) {
                sb.append("  [Empty]\n");
            } else {
                for (ProcessControlBlock p : q) {
                    sb.append(String.format("  - PID: %d, Name: %s, PRI: %d, vruntime: %d\n", 
                            p.getPid(), p.getCommandName(), p.getSchedulingInfo().getPriority(), p.getSchedulingInfo().getVruntime()));
                }
            }
        }

        return CommandResult.success(sb.toString().trim());
    }
}
