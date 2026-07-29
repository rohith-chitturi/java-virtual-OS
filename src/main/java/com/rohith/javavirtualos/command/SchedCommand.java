package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

public class SchedCommand implements Command {
    private final ProcessService processService;

    public SchedCommand(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public String getName() { return "sched"; }

    @Override
    public String getDescription() { return "Shows the active scheduling algorithm for each core"; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        KernelDispatcher dispatcher = processService.getDispatcher();
        if (dispatcher == null) return CommandResult.failure("Scheduler not initialized.");

        StringBuilder sb = new StringBuilder();
        sb.append("Active Scheduling Policies:\n");
        for (int i = 0; i < dispatcher.getCoreSchedulers().size(); i++) {
            sb.append(String.format("Core %d: %s\n", i, dispatcher.getCoreSchedulers().get(i).getName()));
        }
        return CommandResult.success(sb.toString().trim());
    }
}
