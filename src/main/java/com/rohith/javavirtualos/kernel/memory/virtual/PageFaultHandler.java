package com.rohith.javavirtualos.kernel.memory.virtual;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.MemoryEvent.PageEvictedEvent;
import com.rohith.javavirtualos.kernel.events.MemoryEvent.PageLoadedEvent;
import com.rohith.javavirtualos.kernel.memory.virtual.strategy.PageReplacementStrategy;

import java.util.Optional;

public class PageFaultHandler {
    private final FrameTable frameTable;
    private final SwapManager swapManager;
    private final PageReplacementStrategy replacementStrategy;
    private final KernelEventBus eventBus;

    public PageFaultHandler(FrameTable frameTable, SwapManager swapManager, PageReplacementStrategy replacementStrategy, KernelEventBus eventBus) {
        this.frameTable = frameTable;
        this.swapManager = swapManager;
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
            swapManager.store(victimPage);
            
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
            swapManager.load(page);
        }

        pte.setFrame(frame);
        pte.setValid(true);
        pte.setState(PageState.IN_MEMORY);
        
        replacementStrategy.recordAccess(frame);
        eventBus.publish(new PageLoadedEvent(page, frame));
    }

    public void handleCowFault(CowFaultException exception, PageTable pageTable, TLB tlb, PageTableEntry pte) {
        Frame oldFrame = pte.getFrame();
        
        if (oldFrame.getRefCount() <= 1) {
            // Only one reference, we can just remove the write protection
            pte.setWriteProtected(false);
            pte.setShared(false);
            oldFrame.decrementRefCount(); // Not strictly necessary if we rely on isShared, but good practice
            return;
        }
        
        // Need to allocate a new frame
        Optional<FrameTable.FrameTableEntry> freeFrameOpt = frameTable.getFreeFrame();
        Frame newFrame;
        if (freeFrameOpt.isPresent()) {
            FrameTable.FrameTableEntry freeEntry = freeFrameOpt.get();
            freeEntry.setFree(false);
            freeEntry.setOccupant(exception.getPage());
            freeEntry.setReferenced(true);
            newFrame = freeEntry.getFrame();
        } else {
            newFrame = replacementStrategy.chooseVictim(frameTable);
            FrameTable.FrameTableEntry victimEntry = frameTable.getEntries().stream()
                .filter(e -> e.getFrame().equals(newFrame)).findFirst().orElseThrow();
            
            Page victimPage = victimEntry.getOccupant();
            swapManager.store(victimPage);
            
            eventBus.publish(new PageEvictedEvent(victimPage, newFrame));
            tlb.invalidate(victimPage);
            
            // Note: we'd need to find the victim's PTE, but for now this simplistic simulation 
            // suffices. In reality, reverse-mapping is needed.
            
            victimEntry.setOccupant(exception.getPage());
            victimEntry.setReferenced(true);
        }
        
        // Detach from old frame
        oldFrame.decrementRefCount();
        
        // Attach to new frame
        newFrame.incrementRefCount();
        pte.setFrame(newFrame);
        pte.setWriteProtected(false);
        pte.setShared(false);
        pte.setDirty(true);
        
        tlb.update(exception.getPage(), newFrame);
    }

    public void recordAccess(Frame frame) {
        replacementStrategy.recordAccess(frame);
    }
}
