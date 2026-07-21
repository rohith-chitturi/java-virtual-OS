package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.memory.virtual.MMUStatistics;
import com.rohith.javavirtualos.kernel.memory.virtual.strategy.PageReplacementStrategy;
import com.rohith.javavirtualos.shell.ShellContext;

public class PageFaultsCommand implements Command {
    private final MMUStatistics stats;
    private final PageReplacementStrategy strategy;

    public PageFaultsCommand(MMUStatistics stats, PageReplacementStrategy strategy) {
        this.stats = stats;
        this.strategy = strategy;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Total Page Faults : ").append(stats.getPageFaults()).append("\n");
        sb.append("Minor Faults      : ").append(stats.getMinorPageFaults()).append("\n");
        sb.append("Major Faults      : ").append(stats.getMajorPageFaults()).append("\n\n");
        sb.append("Replacement\n-----------\n").append(strategy.getName()).append("\n\n");
        sb.append("TLB Hit Ratio\n-------------\n").append(String.format("%.1f%%", stats.getTlbHitRatio()));
        
        return CommandResult.success(sb.toString());
    }

    @Override
    public String getName() { return "pagefaults"; }
    @Override
    public String getDescription() { return "Show page fault statistics"; }
}
