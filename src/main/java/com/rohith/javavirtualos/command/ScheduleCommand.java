package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.metrics.ExecutionTimeline;
import com.rohith.javavirtualos.shell.ShellContext;
import java.util.List;

public class ScheduleCommand implements Command {
    private final ExecutionTimeline timeline;

    public ScheduleCommand(ExecutionTimeline timeline) {
        this.timeline = timeline;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        List<ExecutionTimeline.TimelineEntry> entries = timeline.getTimeline();
        if (entries.isEmpty()) {
            return CommandResult.success("No execution history available.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s %s\n", "Time", "Execution"));
        
        long startTick = entries.get(0).tick;
        int currentPid = entries.get(0).pid;
        long lastTick = startTick;

        for (int i = 1; i < entries.size(); i++) {
            ExecutionTimeline.TimelineEntry entry = entries.get(i);
            if (entry.pid != currentPid) {
                sb.append(String.format("%d-%d\tPID %d\n", startTick, lastTick, currentPid));
                startTick = entry.tick;
                currentPid = entry.pid;
            }
            lastTick = entry.tick;
        }
        sb.append(String.format("%d-%d\tPID %d\n", startTick, lastTick, currentPid));

        return CommandResult.success(sb.toString().trim());
    }

    @Override
    public String getName() { return "schedule"; }

    @Override
    public String getDescription() { return "View execution timeline"; }
}
