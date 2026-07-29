package com.rohith.javavirtualos.kernel.memory.virtual;

import com.rohith.javavirtualos.kernel.events.MemoryEvent.PageFaultEvent;
import com.rohith.javavirtualos.kernel.events.MemoryEvent.TLBHitEvent;
import com.rohith.javavirtualos.kernel.events.MemoryEvent.TLBMissEvent;
import com.rohith.javavirtualos.kernel.events.*;
import com.rohith.javavirtualos.kernel.memory.PhysicalAddress;
import com.rohith.javavirtualos.kernel.memory.MemoryConstants;

import java.util.Optional;

public class MemoryManagementUnit {
    private final TLB tlb;
    private final PageFaultHandler faultHandler;
    private final MMUStatistics stats;
    private final KernelEventBus eventBus;

    public MemoryManagementUnit(TLB tlb, PageFaultHandler faultHandler, MMUStatistics stats, KernelEventBus eventBus) {
        this.tlb = tlb;
        this.faultHandler = faultHandler;
        this.stats = stats;
        this.eventBus = eventBus;
    }

    public PhysicalAddress translate(VirtualAddress vAddr, com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock pcb, PageTable pageTable) {
        return translate(vAddr, pcb, pageTable, false);
    }

    public PhysicalAddress translate(VirtualAddress vAddr, com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock pcb, PageTable pageTable, boolean isWrite) {
        stats.recordTranslation();
        
        PageSize pageSize = PageSize.STANDARD;
        VirtualMemoryArea targetVma = null;
        for (VirtualMemoryArea vma : pcb.getVmas()) {
            if (vma.contains(vAddr)) {
                targetVma = vma;
                pageSize = vma.getPageSize();
                break;
            }
        }
        
        // If not in a VMA, we could throw a segmentation fault, but we'll default to STANDARD for simulation fallback
        
        long pageSizeBytes = pageSize.getBytes();
        long pageNumber = vAddr.getPageNumber(pageSizeBytes);
        long offset = vAddr.getOffset(pageSizeBytes);
        
        Page page = new Page(pageTable.getPid(), pageNumber);

        Optional<Frame> frameOpt = tlb.lookup(page);
        if (frameOpt.isPresent()) {
            stats.recordTlbHit();
            eventBus.publish(new TLBHitEvent(vAddr));
            Frame frame = frameOpt.get();
            faultHandler.recordAccess(frame);
            return frame.getStartAddress().plus(com.rohith.javavirtualos.kernel.memory.MemorySize.ofBytes(offset));
        }

        stats.recordTlbMiss();
        eventBus.publish(new TLBMissEvent(vAddr));

        PageTableEntry pte = pageTable.getEntry(pageNumber);
        
        if (isWrite && pte.isValid() && pte.isWriteProtected()) {
            eventBus.publish(new MemoryEvent.CowFaultEvent(vAddr));
            CowFaultException cowException = new CowFaultException(vAddr, page);
            faultHandler.handleCowFault(cowException, pageTable, tlb, pte);
        }
        if (!pte.isValid()) {
            if (pte.getState() == PageState.SWAPPED_OUT) {
                stats.recordMajorPageFault();
            } else {
                stats.recordMinorPageFault();
            }
            eventBus.publish(new PageFaultEvent(vAddr));
            
            PageFaultException exception = new PageFaultException(vAddr, page);
            faultHandler.handle(exception, pageTable, tlb);
        }

        pte.setReferenced(true);
        if (isWrite) {
            pte.setDirty(true);
        }
        Frame frame = pte.getFrame();
        tlb.update(page, frame);
        faultHandler.recordAccess(frame);
        
        return frame.getStartAddress().plus(com.rohith.javavirtualos.kernel.memory.MemorySize.ofBytes(offset));
    }
}
