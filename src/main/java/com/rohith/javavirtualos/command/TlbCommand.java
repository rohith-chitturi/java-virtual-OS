package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.memory.virtual.TLB;
import com.rohith.javavirtualos.shell.ShellContext;

public class TlbCommand implements Command {
    private final TLB tlb;

    public TlbCommand(TLB tlb) {
        this.tlb = tlb;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("TLB Contents\n------------\n");
        tlb.getEntries().forEach((page, frame) -> {
            sb.append(page.toString()).append(" -> ").append(frame.toString()).append("\n");
        });
        if (tlb.getEntries().isEmpty()) {
            sb.append("Empty");
        }
        return CommandResult.success(sb.toString());
    }

    @Override
    public String getName() { return "tlb"; }
    @Override
    public String getDescription() { return "View TLB contents"; }
}
