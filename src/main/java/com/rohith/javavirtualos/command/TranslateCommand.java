package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.memory.virtual.*;
import com.rohith.javavirtualos.kernel.memory.MemoryConstants;
import com.rohith.javavirtualos.shell.ShellContext;

public class TranslateCommand implements Command {
    private final MemoryManagementUnit mmu;
    private final TLB tlb;

    public TranslateCommand(MemoryManagementUnit mmu, TLB tlb) {
        this.mmu = mmu;
        this.tlb = tlb;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length < 3) return CommandResult.error("Usage: translate <PID> <VirtualAddress>");
        
        int pid = Integer.parseInt(args[1]);
        long vAddrValue = Long.decode(args[2]);
        VirtualAddress vAddr = new VirtualAddress(vAddrValue);
        
        long pageSizeBytes = MemoryConstants.PAGE_SIZE.toBytes();
        long vpn = vAddr.getPageNumber(pageSizeBytes);
        long offset = vAddr.getOffset(pageSizeBytes);
        
        Page page = new Page(pid, vpn);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Virtual Address : ").append(vAddr).append("\n\n");
        sb.append("VPN            : ").append(vpn).append("\n");
        sb.append("Offset         : 0x").append(Long.toHexString(offset).toUpperCase()).append("\n\n");
        
        boolean tlbHit = tlb.lookup(page).isPresent();
        sb.append("TLB            : ").append(tlbHit ? "HIT" : "MISS").append("\n\n");
        
        PageTable dummyTable = new PageTable(pid);
        try {
            com.rohith.javavirtualos.kernel.memory.PhysicalAddress pAddr = mmu.translate(vAddr, dummyTable);
            PageTableEntry pte = dummyTable.getEntry(vpn);
            sb.append("Page Table     : ").append(pte.isValid() ? "VALID" : "INVALID").append("\n\n");
            sb.append("Frame          : ").append(pte.getFrame() != null ? pte.getFrame().getFrameNumber() : "N/A").append("\n\n");
            sb.append("Physical Addr  : ").append(pAddr).append("\n");
        } catch (Exception e) {
            sb.append("Page Table     : INVALID (PAGE FAULT)\n");
        }

        return CommandResult.success(sb.toString());
    }

    @Override
    public String getName() { return "translate"; }
    @Override
    public String getDescription() { return "Translate virtual address"; }
}
