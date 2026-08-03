package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;
import com.rohith.javavirtualos.kernel.scheduler.cfs.CompletelyFairScheduler;
import com.rohith.javavirtualos.kernel.scheduler.edf.EarliestDeadlineFirstScheduler;
import com.rohith.javavirtualos.kernel.scheduler.mlfq.MultiLevelFeedbackQueueScheduler;
import com.rohith.javavirtualos.kernel.scheduler.roundrobin.RoundRobinScheduler;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.ArrayList;
import java.util.List;

public class SetSchedulerCommand implements Command {
    private final ProcessService processService;

    public SetSchedulerCommand(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public String getName() { return "set-scheduler"; }

    @Override
    public String getDescription() { return "Sets the scheduling algorithm. Usage: set-scheduler <rr|mlfq|edf|cfs>"; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length != 1) return CommandResult.failure("Usage: set-scheduler <rr|mlfq|edf|cfs>");

        KernelDispatcher dispatcher = processService.getDispatcher();
        if (dispatcher == null) return CommandResult.failure("Scheduler not initialized.");

        int numCores = dispatcher.getProcessor().getCoreCount();
        List<Scheduler> newSchedulers = new ArrayList<>();
        String choice = args[0].toLowerCase();

        for (int i = 0; i < numCores; i++) {
            switch (choice) {
                case "rr": newSchedulers.add(new RoundRobinScheduler()); break;
                case "mlfq": newSchedulers.add(new MultiLevelFeedbackQueueScheduler()); break;
                case "edf": newSchedulers.add(new EarliestDeadlineFirstScheduler()); break;
                case "cfs": newSchedulers.add(new CompletelyFairScheduler()); break;
                default: return CommandResult.failure("Unknown scheduler type: " + choice + ". Supported: rr, mlfq, edf, cfs");
            }
        }

        dispatcher.setSchedulers(newSchedulers);
        return CommandResult.success("Scheduler changed to " + choice.toUpperCase() + " across all cores.");
    }
}
