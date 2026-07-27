package com.rohith.javavirtualos.kernel.memory.virtual;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.MemoryEvent.PageEvictedEvent;
import com.rohith.javavirtualos.kernel.events.MemoryEvent.PageLoadedEvent;
import com.rohith.javavirtualos.kernel.memory.virtual.strategy.PageReplacementStrategy;

import java.util.Optional;

public class PageFaultHandler {
    private final FrameTable frameTable;
    private final BackingStore backingStore;
    private final PageReplacementStrategy replacementStrategy;
    private final KernelEventBus eventBus;

    public PageFaultHandler(FrameTable frameTable, BackingStore backingStore, PageReplacementStrategy replacementStrategy, KernelEventBus eventBus) {
        this.frameTable = frameTable;
        this.backingStore = backingStore;
        this.replacementStrategy = replacementStrategy;
        this.eventBus = eventBus;
    }

    public void handle(PageFaultException exception, PageTable pageTable, TLB tlb) {
        Page page = exception.getPage();
        PageTableEntry pte = pageTable.getEntry(page.getPageNumber());

        Optional<FrameTable.FrameTableEntry> freeFrameOpt = frameTable.getFreeFrame();
        Frame frame;
        if (freeFrameOpt.isPresent()) {
            FrameTable.FrameTableEntry freeEntry = freeFrameOpt.get();
            freeEntry.setFree(false);
            freeEntry.setOccupant(page);
            freeEntry.setReferenced(true);
            frame = freeEntry.getFrame();
        } else {
            frame = replacementStrategy.chooseVictim(frameTable);
            FrameTable.FrameTableEntry victimEntry = frameTable.getEntries().stream()
                .filter(e -> e.getFrame().equals(frame)).findFirst().orElseThrow();
            
            Page victimPage = victimEntry.getOccupant();
            backingStore.store(victimPage);
            
            eventBus.publish(new PageEvictedEvent(victimPage, frame));
            tlb.invalidate(victimPage);
            
            if (victimPage.getPid() == pageTable.getPid()) {
                PageTableEntry victimPte = pageTable.getEntry(victimPage.getPageNumber());
                victimPte.setValid(false);
                victimPte.setState(PageState.SWAPPED_OUT);
                victimPte.setFrame(null);
            }
            
            victimEntry.setOccupant(page);
            victimEntry.setReferenced(true);
        }

        if (pte.getState() == PageState.SWAPPED_OUT) {
            backingStore.load(page);
        }

        pte.setFrame(frame);
        pte.setValid(true);
        pte.setState(PageState.IN_MEMORY);
        
        replacementStrategy.recordAccess(frame);
        eventBus.publish(new PageLoadedEvent(page, frame));
    }

    public void recordAccess(Frame frame) {
        replacementStrategy.recordAccess(frame);
    }
}
