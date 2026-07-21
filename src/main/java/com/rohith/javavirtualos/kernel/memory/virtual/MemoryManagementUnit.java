package com.rohith.javavirtualos.kernel.memory.virtual;

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

    public PhysicalAddress translate(VirtualAddress vAddr, PageTable pageTable) {
        stats.recordTranslation();
        long pageSizeBytes = MemoryConstants.PAGE_SIZE.toBytes();
        long pageNumber = vAddr.getPageNumber(pageSizeBytes);
        long offset = vAddr.getOffset(pageSizeBytes);
        Page page = new Page(pageTable.getPid(), pageNumber);

        Optional<Frame> frameOpt = tlb.lookup(page);
        if (frameOpt.isPresent()) {
            stats.recordTlbHit();
            eventBus.publish(new TLBHitEvent(vAddr));
            return frameOpt.get().getStartAddress().plus(com.rohith.javavirtualos.kernel.memory.MemorySize.ofBytes(offset));
        }

        stats.recordTlbMiss();
        eventBus.publish(new TLBMissEvent(vAddr));

        PageTableEntry pte = pageTable.getEntry(pageNumber);
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
        Frame frame = pte.getFrame();
        tlb.update(page, frame);
        
        return frame.getStartAddress().plus(com.rohith.javavirtualos.kernel.memory.MemorySize.ofBytes(offset));
    }
}
