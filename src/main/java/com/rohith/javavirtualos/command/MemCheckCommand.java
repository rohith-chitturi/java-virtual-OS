package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.memory.*;
import com.rohith.javavirtualos.shell.ShellContext;

public class MemCheckCommand implements Command {
    private final MemoryManager manager;

    public MemCheckCommand(MemoryManager manager) {
        this.manager = manager;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Memory Integrity Check\n\n");

        try {
            manager.getValidator().validate(manager.getMemoryMap(), manager.getStatistics().getTotalMemory());
            sb.append("✓ No overlapping blocks\n");
            sb.append("✓ Address ranges valid\n");
            sb.append("✓ Free list consistent\n");
            sb.append("✓ Block count: ").append(manager.getMemoryMap().getBlocks().size()).append("\n");
            sb.append(String.format("✓ Fragmentation: %.1f%%\n\n", manager.getAnalyzer().fragmentationPercentage(manager.getMemoryMap())));
            sb.append("✓ Validation PASSED");
            return CommandResult.success(sb.toString());
        } catch (Exception e) {
            sb.append("✗ Validation FAILED: ").append(e.getMessage());
            return CommandResult.error(sb.toString());
        }
    }

    @Override
    public String getName() { return "memcheck"; }
    @Override
    public String getDescription() { return "Run memory validator"; }
}
