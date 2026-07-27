package com.rohith.javavirtualos.kernel.memory;

import java.util.ArrayList;
import java.util.List;

public class MemoryMap {
    private final List<MemoryBlock> blocks;

    public MemoryMap(MemorySize totalSize, MemorySize reservedKernelSize) {
        this.blocks = new ArrayList<>();
        
        if (reservedKernelSize.toBytes() > 0) {
            MemoryBlock kernelBlock = new MemoryBlock(new PhysicalAddress(0), reservedKernelSize, MemoryState.RESERVED);
            this.blocks.add(kernelBlock);
        }
        
        MemorySize freeSize = totalSize.subtract(reservedKernelSize);
        if (freeSize.toBytes() > 0) {
            MemoryBlock freeBlock = new MemoryBlock(new PhysicalAddress(reservedKernelSize.toBytes()), freeSize, MemoryState.FREE);
            this.blocks.add(freeBlock);
        }
    }

    public List<MemoryBlock> getBlocks() {
        return blocks;
    }

    public MemorySize getFreeMemory() {
        return MemorySize.ofBytes(blocks.stream()
                .filter(b -> b.getState() == MemoryState.FREE)
                .mapToLong(b -> b.getSize().toBytes())
                .sum());
    }

    public void splitBlock(MemoryBlock originalFreeBlock, MemorySize newSize, int pid, int allocationId) {
        int index = blocks.indexOf(originalFreeBlock);
        if (index == -1) throw new IllegalArgumentException("Block not in map");

        MemorySize remaining = originalFreeBlock.getSize().subtract(newSize);
        
        originalFreeBlock.setSize(newSize);
        originalFreeBlock.setState(MemoryState.ALLOCATED);
        originalFreeBlock.setPid(pid);
        originalFreeBlock.setAllocationId(allocationId);

        if (remaining.toBytes() > 0) {
            PhysicalAddress nextStart = originalFreeBlock.getEnd();
            MemoryBlock remainderBlock = new MemoryBlock(nextStart, remaining, MemoryState.FREE);
            blocks.add(index + 1, remainderBlock);
        }
    }

    public void mergeFreeBlocks() {
        for (int i = 0; i < blocks.size() - 1; i++) {
            MemoryBlock current = blocks.get(i);
            MemoryBlock next = blocks.get(i + 1);
            if (current.getState() == MemoryState.FREE && next.getState() == MemoryState.FREE) {
                current.setSize(current.getSize().add(next.getSize()));
                blocks.remove(i + 1);
                i--;
            }
        }
    }
}
