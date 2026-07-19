package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.memory.*;
import com.rohith.javavirtualos.shell.ShellContext;

public class MemInfoCommand implements Command {
    private final MemoryManager manager;

    public MemInfoCommand(MemoryManager manager) {
        this.manager = manager;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        MemoryStatistics stats = manager.getStatistics();
        FragmentationAnalyzer analyzer = manager.getAnalyzer();
        MemoryMap map = manager.getMemoryMap();

        StringBuilder sb = new StringBuilder();
        sb.append("========= Memory =========\n\n");
        sb.append("Allocator\n").append(manager.getStrategyName()).append("\n\n");
        sb.append("Total\n").append(stats.getTotalMemory()).append("\n\n");
        sb.append("Used\n").append(stats.getUsedMemory()).append("\n\n");
        sb.append("Free\n").append(stats.getFreeMemory()).append("\n\n");
        sb.append("Largest Hole\n").append(analyzer.largestHole(map)).append("\n\n");
        sb.append("Smallest Hole\n").append(analyzer.smallestHole(map)).append("\n\n");
        sb.append(String.format("Fragmentation\n%.1f%%\n\n", analyzer.fragmentationPercentage(map)));
        sb.append("Allocations\n").append(stats.getAllocationCount()).append("\n\n");
        sb.append("Deallocations\n").append(stats.getDeallocationCount()).append("\n\n");
        sb.append("OOM Events\n").append(stats.getOomCount());

        return CommandResult.success(sb.toString());
    }

    @Override
    public String getName() { return "meminfo"; }
    @Override
    public String getDescription() { return "Detailed memory statistics"; }
}
