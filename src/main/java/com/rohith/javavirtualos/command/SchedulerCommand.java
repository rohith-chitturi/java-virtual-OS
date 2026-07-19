package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.core.CPU;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;
import com.rohith.javavirtualos.shell.ShellContext;

public class SchedulerCommand implements Command {
    private final Scheduler scheduler;
    private final CPU cpu;
    private final int quantum;

    public SchedulerCommand(Scheduler scheduler, CPU cpu, int quantum) {
        this.scheduler = scheduler;
        this.cpu = cpu;
        this.quantum = quantum;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Current Scheduler\n").append(scheduler.getName()).append("\n\n");
        sb.append("Quantum\n").append(quantum).append(" ms\n\n");
        sb.append("Ready Queue\n").append(scheduler.getReadyQueue().size()).append("\n\n");
        
        ProcessControlBlock current = cpu.getCurrentProcess();
        sb.append("Running\n").append(current != null ? "PID " + current.getPid() : "None").append("\n\n");
        
        sb.append("Waiting\n").append(scheduler.getReadyQueue().size());

        return CommandResult.success(sb.toString());
    }

    @Override
    public String getName() { return "scheduler"; }

    @Override
    public String getDescription() { return "Show current scheduler state"; }
}
