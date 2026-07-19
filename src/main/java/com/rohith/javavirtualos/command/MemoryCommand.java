package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.memory.*;
import com.rohith.javavirtualos.shell.ShellContext;

public class MemoryCommand implements Command {
    private final MemoryManager manager;

    public MemoryCommand(MemoryManager manager) {
        this.manager = manager;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        MemoryMap map = manager.getMemoryMap();
        long totalBytes = manager.getStatistics().getTotalMemory().toBytes();
        
        int barLength = 50;
        StringBuilder visual = new StringBuilder();
        
        for (MemoryBlock block : map.getBlocks()) {
            int chars = (int) Math.round((double) block.getSize().toBytes() / totalBytes * barLength);
            char c;
            switch (block.getState()) {
                case RESERVED: c = '#'; break;
                case ALLOCATED: c = '█'; break;
                case FREE: default: c = '░'; break;
            }
            for (int i = 0; i < chars; i++) {
                visual.append(c);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Memory\n").append(visual.toString()).append("\n\n");
        sb.append("Total\n").append(manager.getStatistics().getTotalMemory()).append("\n\n");
        sb.append("Used\n").append(manager.getStatistics().getUsedMemory()).append("\n\n");
        sb.append("Free\n").append(manager.getStatistics().getFreeMemory()).append("\n\n");
        sb.append(String.format("Fragmentation\n%.1f%%\n", manager.getAnalyzer().fragmentationPercentage(map)));

        return CommandResult.success(sb.toString());
    }

    @Override
    public String getName() { return "memory"; }
    @Override
    public String getDescription() { return "Visualize memory map"; }
}
