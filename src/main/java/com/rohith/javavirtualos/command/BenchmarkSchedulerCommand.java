package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.pcb.SchedulingInfo;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;
import com.rohith.javavirtualos.kernel.scheduler.cfs.CompletelyFairScheduler;
import com.rohith.javavirtualos.kernel.scheduler.edf.EarliestDeadlineFirstScheduler;
import com.rohith.javavirtualos.kernel.scheduler.mlfq.MultiLevelFeedbackQueueScheduler;
import com.rohith.javavirtualos.kernel.scheduler.roundrobin.RoundRobinScheduler;

import com.rohith.javavirtualos.shell.ShellContext;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkSchedulerCommand implements Command {
    public BenchmarkSchedulerCommand() {
    }

    @Override
    public String getName() { return "benchmark-scheduler"; }

    @Override
    public String getDescription() { return "Runs a deterministic workload against all schedulers to compare performance."; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Running Scheduler Benchmarks...\n\n");

        Scheduler[] schedulers = {
            new RoundRobinScheduler(),
            new MultiLevelFeedbackQueueScheduler(),
            new EarliestDeadlineFirstScheduler(),
            new CompletelyFairScheduler()
        };

        for (Scheduler s : schedulers) {
            sb.append(String.format("%s\n", s.getName()));
            
            // Generate deterministic workload
            List<ProcessControlBlock> workload = createWorkload();
            
            // Simulate scheduler processing
            for (ProcessControlBlock p : workload) {
                s.addProcess(p);
            }

            int totalWait = 0;
            int totalTurnaround = 0;
            long tick = 0;

            while (!s.getReadyQueue().isEmpty()) {
                ProcessControlBlock p = s.nextProcess();
                if (p != null) {
                    SchedulingInfo info = p.getSchedulingInfo();
                    
                    // Simulate wait and turnaround based on simple rules
                    totalWait += (int) (tick - info.getArrivalTime());
                    
                    long burst = info.getBurstTime();
                    if (burst <= 0) burst = 10;
                    tick += burst; // simulate execution
                    
                    totalTurnaround += (int) (tick - info.getArrivalTime());
                } else {
                    tick++;
                }
            }

            int avgWait = workload.size() > 0 ? totalWait / workload.size() : 0;
            int avgTurnaround = workload.size() > 0 ? totalTurnaround / workload.size() : 0;

            sb.append(String.format("Average wait: %d ticks\n", avgWait));
            sb.append(String.format("Average turnaround: %d ticks\n\n", avgTurnaround));
        }

        return CommandResult.success(sb.toString().trim());
    }

    private List<ProcessControlBlock> createWorkload() {
        List<ProcessControlBlock> list = new ArrayList<>();
        // Mock 5 processes with varying bursts and arrivals
        // We only populate SchedulingInfo for the benchmark simulation
        for (int i = 1; i <= 5; i++) {
            ProcessControlBlock pcb = new ProcessControlBlock(i, i, i, 1, "task" + i, null, null, new SchedulingInfo(0, 0), null);
            pcb.getSchedulingInfo().setBurstTime(i * 10);
            pcb.getSchedulingInfo().setDeadline(i * 20);
            list.add(pcb);
        }
        return list;
    }
}
